package clarity.backend.entity

import clarity.backend.DataManager
import java.sql.Statement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.Exception

// Request Formats.
data class CreateCardSetEntity(val creator_id: Int, val title: String, val type: String, val progress: Int)
data class AddCardToSetRequest(val card_id: Int, val set_id: Int)
data class DeleteCardFromSetRequest(val card_id: Int, val set_id: Int)
data class GetCardsInSetRequest(val set_id: Int)
data class GetDataForSetRequest(val set_id: Int)
data class GetSetsByUsername(val username: String)
data class GetProgressForSetRequest(val set_id: Int)
data class UpdateProgressForSetRequest(val set_id: Int, val progress: Int)
data class CompleteCardRequest(val user_id: Int, val card: Int, val set: Int)
data class LikeCardSetRequest(val user_id: Int, val set_id: Int)
data class UnlikeCardSetRequest(val user_id: Int, val set_id: Int)
data class ToggleCardSetRequest(val set_id: Int)
data class ClonePublicSetRequest(val set_id: Int, val user_id: Int)
data class GetSetDataRequest(val set_id: Int)
data class GetCardSetsForFollowingRequest(val user_id: Int)

// Response Formats.
data class CreateCardSetResponse(val response: StatusResponse, val msg: String, val set: SetMetadata? = null)
data class AddCardToSetResponse(val response: StatusResponse, val msg: String)
data class DeleteCardFromSetResponse(val response: StatusResponse, val msg: String)
data class GetCardsInSetResponse(val response: StatusResponse, val cards: List<Card>)
data class GetSetIDsResponse(val response: StatusResponse, val sets: List<String>)
data class GetSetDataResponse(val response: StatusResponse, val set: CardSet? = null)
data class GetDataForSetResponse(val response: StatusResponse, val data: List<String>)
data class GetSetsByUsernameResponse(val response: StatusResponse, val data: List<SetMetadata>)
data class CompleteCardResponse(val response: StatusResponse, val msg: String, val card_id: Int, val set_id: Int, val user_id: Int)
data class GetCompletedCardsInSetResponse(val response: StatusResponse, val cards: List<CardInSet>)
data class ToggleCardSetResponse(val response: StatusResponse, val is_public: Int)
data class GetPublicCardSetsResponse(val response: StatusResponse, val sets: List<SetMetadata>)
data class ClonePublicSetResponse(val response: StatusResponse, val new_set_id: Int, val msg: String)
data class GetCardSetsForFollowingResponse(
    val response: StatusResponse, 
    val data: List<UserCreatedCardSet>, 
    val msg: String
)


data class GetUserSetProgressResponse(
    val response: StatusResponse, 
    val set_id: Int, 
    val user_id: Int, 
    val numCards: Int, 
    val numCompletedCards: Int, 
    val cards: List<Card>, 
    val completedCard: List<CardInSet>
)
data class getPublicCardSetsOrderedByLikesResponse(val response: StatusResponse, val sets: List<SetMetadata>)
data class LikeCardSetResponse(val response: StatusResponse, val message: String)
data class UnlikeCardSetResponse(val response: StatusResponse, val message: String)

// Util Formats

// Represents all the card sets created by user_id.
data class UserCreatedCardSet(
    val user_id: Int,
    val card_sets: List<CardSet>
)

data class CardSet(
    val metadata: SetMetadata,
    val cards: List<Card>
)

data class SetMetadata(
    val set_id: Int,
    val creator_id: Int, 
    val title: String, 
    val type: String, 
    val is_public: Boolean, 
    val likes: Int, 
    val cloned_from_set: Int
)
data class GetUserSetProgressRequest(val set_id: Int, val user_id: Int)
data class CardInSet(val card_id: Int, val set_id: Int, val completion_date: String?)

class CardSetEntity() {
    fun createCardSet(set: CreateCardSetEntity) : CreateCardSetResponse {
        val db = DataManager.conn()
        var newSet: SetMetadata? = null
        try {
            val statement = db!!.createStatement()
            val query = """
                INSERT INTO CardSet(creator_id, title, type, is_public_ind, likes, cloned_from_set)
                VALUES (${set.creator_id}, '${set.title}', '${set.type}', 0, 0, NULL);
            """.trimIndent()
            val insertedRows = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS)

            if(insertedRows > 0) {
                val keys = statement.generatedKeys
                keys.next()

                newSet = SetMetadata(
                    set_id =  keys.getInt(1),
                    creator_id = set.creator_id,
                    title = set.title,
                    type = set.type,
                    false,
                    0,
                    0
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val errMsg: String = "Failed to create card set: ${e.message ?: "Unknown error"}"
            return CreateCardSetResponse(StatusResponse.Failure, errMsg)
        }
        return CreateCardSetResponse(StatusResponse.Success, "Created Card Set!", newSet)
    }

    fun addCardToSet(card: AddCardToSetRequest) : AddCardToSetResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val query = """
                INSERT INTO CardInSet([set_id], card_id, completion_date)
                VALUES (${card.set_id}, ${card.card_id}, NULL);
            """.trimIndent()
            statement.executeUpdate(query)
        } catch (e: Exception) {
            e.printStackTrace()
            val errMsg: String = "Failed to add card to set: ${e.message ?: "Unknown error"}"
            return AddCardToSetResponse(StatusResponse.Failure, errMsg)
        }
        return AddCardToSetResponse(StatusResponse.Success, "Added card ${card.card_id} to set.")
    }

    fun deleteCardFromSet(card: DeleteCardFromSetRequest) : DeleteCardFromSetResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val query = """
                DELETE FROM CardInSet
                WHERE [set_id] = ${card.set_id} AND card_id = ${card.card_id};
            """.trimIndent()
            statement.executeUpdate(query)
        } catch (e: Exception) {
            e.printStackTrace()
            val errMsg: String = "Failed to delete card from set: ${e.message ?: "Unknown error"}"
            return DeleteCardFromSetResponse(StatusResponse.Failure, errMsg)
        }
        return DeleteCardFromSetResponse(StatusResponse.Success, "Deleted card from set.")
    }

    fun getSetData(request: GetSetDataRequest): GetSetDataResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val query = "SELECT * FROM CardSet WHERE [set_id] = ${request.set_id};"
            val row = statement.executeQuery(query)
            if (row.next()) {
                val get_total_cards_resp = this.getTotalCardsFromSet(
                    GetCardsInSetRequest(set_id=request.set_id)
                )

                if (get_total_cards_resp.response == StatusResponse.Failure) {
                    return GetSetDataResponse(StatusResponse.Failure, null)
                }

                val cards = get_total_cards_resp.cards

                return GetSetDataResponse(
                    StatusResponse.Success,
                    CardSet(
                        metadata=SetMetadata(
                            set_id = row.getInt("set_id"),
                            creator_id = row.getInt("creator_id"),
                            title = row.getString("title"),
                            type = row.getString("type"),
                            is_public = (row.getInt("is_public_ind") == 1),
                            likes = row.getInt("likes"),
                            cloned_from_set = row.getInt("cloned_from_set")
                        ),
                        cards=cards
                    )
                )
            } else {
                return GetSetDataResponse(StatusResponse.Failure, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return GetSetDataResponse(StatusResponse.Failure, null)
        }
    }

    fun getTotalCardsFromSet(set: GetCardsInSetRequest) : GetCardsInSetResponse {
        val db = DataManager.conn()

        try {
            val statement = db!!.createStatement()
            val query = """
                SELECT c.card_id, ca.phrase, ca.title FROM CardInSet c, Card ca
                WHERE c.[set_id] = ${set.set_id} AND ca.card_id = c.card_id
            """.trimIndent()
            val resultSet = statement.executeQuery(query)
            val cardIdList = mutableListOf<Card>()

            // Extract the card ids and convert to a list of strings.
            while (resultSet.next()) {
                cardIdList.add(
                    Card(
                        card_id = resultSet.getInt("card_id"),
                        phrase = resultSet.getString("phrase"),
                        title = resultSet.getString("title")
                    )
                )
            }
            resultSet.close()

            return GetCardsInSetResponse(StatusResponse.Success, cardIdList.toList())

        } catch (e: Exception) {
            e.printStackTrace()
            return GetCardsInSetResponse(StatusResponse.Failure, emptyList())
        }
    }

    fun getSetIDs() : GetSetIDsResponse {
        val db = DataManager.conn()

        try {
            val statement = db!!.createStatement()
            val query = "SELECT [set_id] FROM CardSet"
            val resultSet = statement.executeQuery(query)
            val setList = mutableListOf<String>()

            // Extract the card ids and convert to a list of strings.
            while (resultSet.next()) {
                val setId = resultSet.getString("set_id")
                setList.add(setId)
            }
            resultSet.close()
            return GetSetIDsResponse(StatusResponse.Success, setList.toList())
        } catch (e: Exception) {
            e.printStackTrace()
            return GetSetIDsResponse(StatusResponse.Failure, emptyList())
        }
    }

    fun getSetsByUsername(request: GetSetsByUsername) : GetSetsByUsernameResponse {
        val db = DataManager.conn();
        val username = request.username
        try {
            val statement = db!!.createStatement();
            val query = """
                SELECT c.[set_id], c.creator_id, c.title, c.type, c.is_public_ind, c.likes, c.cloned_from_set
                FROM CardSet c, User u
                WHERE u.username = '$username' AND u.user_id = c.creator_id;
            """.trimIndent()
            val resultSet = statement.executeQuery(query);

            val setList = mutableListOf<SetMetadata>()
            while(resultSet.next()) {
                val set = SetMetadata(
                    set_id = resultSet.getInt("set_id"),
                    creator_id = resultSet.getInt("creator_id"),
                    title = resultSet.getString("title"),
                    type = resultSet.getString("type"),
                    is_public = resultSet.getInt("is_public_ind") == 1,
                    likes = resultSet.getInt("likes"),
                    // NULL is set to 0, so it indicates a new set.
                    cloned_from_set = resultSet.getInt("cloned_from_set") 
                )
                setList.add(set)
            }
            resultSet.close()

            return GetSetsByUsernameResponse(StatusResponse.Success, setList)
        } catch (e: Exception) {
            e.printStackTrace()
            return GetSetsByUsernameResponse(StatusResponse.Failure, emptyList())
        }
    }

    fun completeCardInUserSet(request: CompleteCardRequest): CompleteCardResponse {
        val db = DataManager.conn()
        val (user_id, card, set) = request
        try {
            val statement = db!!.createStatement()
            val localTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
            val updateQuery = "UPDATE CardInSet SET completion_date = '$localTime' WHERE [set_id] = $set AND card_id = $card"

            val updatedRows = statement.executeUpdate(updateQuery)

            return if(updatedRows > 0) {
                CompleteCardResponse(
                    set_id = set,
                    card_id = card,
                    user_id = user_id,
                    response = StatusResponse.Success,
                    msg = "Card completed successfully."
                )
            } else {
                CompleteCardResponse(
                    set_id = set,
                    card_id = card,
                    user_id = user_id,
                    response = StatusResponse.Failure,
                    msg = "Could not complete card"
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return CompleteCardResponse(
                set_id = set,
                card_id = card,
                user_id = user_id,
                response = StatusResponse.Failure,
                msg = e.message ?: "Unknown error"
            )
        }
    }

    private fun getCompletedCardsForSet(set: Int): GetCompletedCardsInSetResponse {
        val conn = DataManager.conn()
        try {
            val statement = conn!!.createStatement()
            val selectQuery = "SELECT * FROM CardInSet WHERE completion_date IS NOT NULL AND [set_id] = $set"
            val response = statement.executeQuery(selectQuery)

            val cardsInSet = mutableListOf<CardInSet>()

            while(response.next()) {
                cardsInSet.add(
                    CardInSet(
                        card_id = response.getInt("card_id"),
                        set_id = response.getInt("set_id"),
                        completion_date = response.getString("completion_date")
                    )
                )
            }

            return GetCompletedCardsInSetResponse(
                StatusResponse.Success,
                cardsInSet
            )
        } catch (e: Exception) {
            e.printStackTrace();
            return GetCompletedCardsInSetResponse(
                StatusResponse.Failure,
                mutableListOf()
            )
        }
    }

    fun getUserSetProgress(request: GetUserSetProgressRequest): GetUserSetProgressResponse {
        val (set, user) = request
        try {
            val cards = this.getTotalCardsFromSet(GetCardsInSetRequest(set))
            val numCards = cards.cards.size

            val completedCards = this.getCompletedCardsForSet(set)
            val numCompletedCards = completedCards.cards.size

            return GetUserSetProgressResponse(
                response = StatusResponse.Success,
                set_id = set,
                user_id = user,
                numCards = numCards,
                numCompletedCards = numCompletedCards,
                cards = cards.cards,
                completedCard = completedCards.cards
            )
        } catch (e: Exception) {
            e.printStackTrace();
            return GetUserSetProgressResponse(
                response = StatusResponse.Failure,
                set_id = set,
                user_id = user,
                numCards = 0,
                numCompletedCards = 0,
                cards = listOf(),
                completedCard = listOf()
            )
        }
    }

    fun getPublicCardSetsOrderedByLikes() : getPublicCardSetsOrderedByLikesResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val query = "SELECT * FROM CardSet WHERE is_public_ind = 1 ORDER BY likes DESC;"
            val resultSet =  statement.executeQuery(query)
            val sets = mutableListOf<SetMetadata>()

            while (resultSet.next()) {
                val set = SetMetadata(
                    set_id = resultSet.getInt("set_id"),
                    creator_id = resultSet.getInt("creator_id"),
                    title = resultSet.getString("title"),
                    type = resultSet.getString("type"),
                    is_public = resultSet.getInt("is_public_ind") == 1,
                    likes = resultSet.getInt("likes"),
                    cloned_from_set = resultSet.getInt("cloned_from_set")
                )
                sets.add(set)
            }
            resultSet.close()
            return getPublicCardSetsOrderedByLikesResponse(StatusResponse.Success, sets)
        } catch (e: Exception) {
            e.printStackTrace()
            return getPublicCardSetsOrderedByLikesResponse(StatusResponse.Failure, emptyList())
        }
    }

    fun likeCardSet(request: LikeCardSetRequest): LikeCardSetResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            var query = """
                SELECT * FROM SetLikes
                WHERE user_id = ${request.user_id} AND [set_id] = ${request.set_id};
            """.trimIndent()
            val result = statement.executeQuery(query)

            // If we don't already have an entry, then we make the necessary updates.
            if (!result.next()) {
                query = """
                    INSERT INTO SetLikes (user_id, [set_id])
                    VALUES (${request.user_id}, ${request.set_id});
                """.trimIndent()
                statement.executeUpdate(query)
                query = "UPDATE CardSet SET likes = likes + 1 WHERE [set_id] = ${request.set_id};"
                statement.executeUpdate(query)
            }
            return LikeCardSetResponse(StatusResponse.Success, "")
        } catch (e: Exception) {
            e.printStackTrace()
            return LikeCardSetResponse(StatusResponse.Failure, e.message ?: "Unknown error occured.")
        }
    }

    fun unlikeCardSet(request: UnlikeCardSetRequest): UnlikeCardSetResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            var query = """
                SELECT * FROM SetLikes
                WHERE user_id = ${request.user_id} AND [set_id] = ${request.set_id};
            """.trimIndent()
            val result = statement.executeQuery(query)

            // If we don't have an entry, then the user has not liked the set, so do nothing.
            if (result.next()) {
                query = "DELETE FROM SetLikes WHERE user_id = ${request.user_id} AND [set_id] = ${request.set_id};"
                statement.executeUpdate(query)
                query = "UPDATE CardSet SET likes = likes - 1 WHERE [set_id] = ${request.set_id};"
                statement.executeUpdate(query)
            }
            return UnlikeCardSetResponse(StatusResponse.Success, "")
        } catch (e: Exception) {
            e.printStackTrace()
            return UnlikeCardSetResponse(StatusResponse.Failure, e.message ?: "Unknown error occured.")
        }
    }

    fun toggleCardSetVisibility(request: ToggleCardSetRequest): ToggleCardSetResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            var query = "SELECT is_public_ind FROM CardSet WHERE [set_id] = ${request.set_id};"
            val result = statement.executeQuery(query)

            if (result.next()) {
                val is_public = result.getInt("is_public_ind")
                // If is_public = 0 --> new_mode = 1. Otherwise, is_public = 1 --> new_mode = 0.
                val new_mode = 1 - is_public
                query = "UPDATE CardSet SET is_public_ind = $new_mode WHERE [set_id] = ${request.set_id};"
                statement.executeUpdate(query)
                return ToggleCardSetResponse(StatusResponse.Success, new_mode)
            }
            // We can't toggle visibility of a card set that does not exist in the CardSet table.
            return ToggleCardSetResponse(StatusResponse.Failure, -1)
        } catch (e: Exception) {
            e.printStackTrace()
            return ToggleCardSetResponse(StatusResponse.Failure, -1)
        }
    }

    fun getPublicCardSets(): GetPublicCardSetsResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val query = "SELECT * FROM CardSet WHERE is_public_ind = 1;"
            val rows = statement.executeQuery(query)
            val sets = mutableListOf<SetMetadata>()

            while (rows.next()) {
                val currentSet = SetMetadata(
                    set_id = rows.getInt("set_id"),
                    creator_id = rows.getInt("creator_id"),
                    title = rows.getString("title"),
                    type = rows.getString("type"),
                    is_public = true,
                    likes = rows.getInt("likes"),
                    cloned_from_set = rows.getInt("cloned_from_set")
                )
                sets.add(currentSet)
            }
            return GetPublicCardSetsResponse(StatusResponse.Success, sets.toList())
        } catch (e: Exception) {
            e.printStackTrace()
            return GetPublicCardSetsResponse(StatusResponse.Failure, emptyList())
        }
    }

    fun clonePublicSet(request: ClonePublicSetRequest): ClonePublicSetResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val getSetDataResp = this.getSetData(GetSetDataRequest(set_id=request.set_id))
            val setData = getSetDataResp.set
            
            // If we couldn't get the associated set data, then we return failure.
            if (getSetDataResp.response == StatusResponse.Failure) {
                return ClonePublicSetResponse(
                    StatusResponse.Failure, 
                    -1, 
                    "Could not get data of set (${request.set_id}) for cloning."
                )
            }

            // If the response was successful, then the data is present.
            val metadata: SetMetadata = setData!!.metadata
            val cards: List<Card> = setData!!.cards

            // We cannot clone private sets.
            if (!metadata.is_public) {
                return ClonePublicSetResponse(
                    StatusResponse.Failure,
                    -1,
                    "Cannot clone private set (set_id: ${request.set_id})."
                )
            }
            
            // Insert the new set and get back the set id.
            val insertQuery = """
                INSERT INTO CardSet (creator_id, title, type, is_public_ind, likes, cloned_from_set)
                VALUES (${request.user_id}, '${metadata.title}', '${metadata.type}', 0, 0, ${metadata.set_id});
            """.trimIndent()

            val affectedRows: Int = statement.executeUpdate(insertQuery)

            if (affectedRows <= 0) {
                return ClonePublicSetResponse(
                    StatusResponse.Failure, 
                    -1, 
                    "Could not insert cloned set (cloned_set_id: ${request.set_id}) into CardSet table."
                )
            }

            // If the above insert passed, get the associated set_id.
            val result = statement.executeQuery("SELECT last_insert_rowid() AS new_set_id;")
            var new_set_id = -1

            if (result.next()) {
                new_set_id = result.getInt("new_set_id")

                // Now add the same cards to the new set as the one we want to clone.
                for (card in cards) {
                    val resp: AddCardToSetResponse = 
                        this.addCardToSet(AddCardToSetRequest(card_id=card.card_id, set_id=new_set_id))
                    if (resp.response == StatusResponse.Failure) {
                        return ClonePublicSetResponse(StatusResponse.Failure, -1, resp.msg)
                    }
                }
                
            } else {
                return ClonePublicSetResponse(
                    StatusResponse.Failure,
                    -1,
                    "Could not get set_id for the newly cloned set that was inserted into CardSet."
                )
            }

            return ClonePublicSetResponse(StatusResponse.Success, new_set_id, "")
        } catch (e: Exception) {
            e.printStackTrace()
            return ClonePublicSetResponse(StatusResponse.Failure, -1, e.message ?: "Unknown Error")
        }
    }

    fun getCardSetsForFollowing(request: GetCardSetsForFollowingRequest): GetCardSetsForFollowingResponse {
        val db = DataManager.conn()
        try {
            val following_resp: FollowerListResponse = FollowingEntity().getFollowing(request.user_id);
            if (following_resp.response == StatusResponse.Failure) {
                return GetCardSetsForFollowingResponse(
                    StatusResponse.Failure,
                    emptyList(),
                    "Could not get users that User (${request.user_id}) follows from /getFollowing endpoint."
                )
            }

            // Get the user ids that request.user_id follows.
            val following: List<Int> = following_resp.followers

            if (following.isEmpty()) {
                return GetCardSetsForFollowingResponse(
                    StatusResponse.Success,
                    emptyList(),
                    "User (${request.user_id}) is not following anyone."
                )
            }

            // Get PUBLIC sets created by users that request.user_id is following.
            val statement = db!!.createStatement()
            val id_list = following.joinToString(", ")
            val query = """
                SELECT [set_id], creator_id FROM CardSet
                WHERE creator_id IN (${id_list}) AND is_public_ind = 1;
            """.trimIndent()
            val resultRows = statement.executeQuery(query)
            var data_map = mutableMapOf<Int, MutableList<CardSet>>();

            while (resultRows.next()) {
                val set_id = resultRows.getInt("set_id")
                val creator_id = resultRows.getInt("creator_id")
                val set_data_resp: GetSetDataResponse = this.getSetData(GetSetDataRequest(set_id));

                if (set_data_resp.response == StatusResponse.Failure) {
                    return GetCardSetsForFollowingResponse(
                        StatusResponse.Failure,
                        emptyList(),
                        "Could not get set data for Set (${set_id}) in /getCardSetsForFollowing"
                    )
                }

                if (data_map.containsKey(creator_id)) {
                    data_map[creator_id]!!.add(set_data_resp.set!!)
                } else {
                    data_map[creator_id] = mutableListOf<CardSet>(set_data_resp.set!!)
                }
            }

            var data = mutableListOf<UserCreatedCardSet>()
            for ((user_id, cardset_list) in data_map) {
                data.add(UserCreatedCardSet(user_id=user_id, card_sets=cardset_list.toList()))
            }

            return GetCardSetsForFollowingResponse(
                StatusResponse.Success,
                data.toList(),
                ""
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return GetCardSetsForFollowingResponse(
                StatusResponse.Failure, 
                emptyList(), 
                e.message ?: "Unknown error in /getCardSetsForFollowing")
        }
    }
}