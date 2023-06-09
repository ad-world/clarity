package clarity.backend.entity

import clarity.backend.DataManager
import java.lang.Exception
import java.sql.Statement

// Request Formats.
data class CreateCardSetEntity(val creator_id: Int, val title: String, val type: String, val progress: Int)
data class AddCardToSetRequest(val card_id: Int, val set_id: Int)
data class DeleteCardFromSetRequest(val card_id: Int, val set_id: Int)
data class GetCardsInSetRequest(val set_id: Int)
data class GetDataForSetRequest(val set_id: Int)
data class GetSetsByUsername(val username: String)
data class GetProgressForSetRequest(val set_id: Int)
data class UpdateProgressForSetRequest(val set_id: Int, val progress: Int)

// Response Formats.
data class CreateCardSetResponse(val response: StatusResponse, val msg: String, val set: SetMetadata? = null)
data class AddCardToSetResponse(val response: StatusResponse, val msg: String)
data class DeleteCardFromSetResponse(val response: StatusResponse, val msg: String)
data class GetCardsInSetResponse(val response: StatusResponse, val cards: List<Card>)
data class GetSetsResponse(val response: StatusResponse, val sets: List<String>)
data class GetDataForSetResponse(val response: StatusResponse, val data: List<String>)
data class GetSetsByUsernameResponse(val response: StatusResponse, val data: List<SetMetadata>)
data class GetProgressForSetResponse(val response: StatusResponse, val progress: Int)
data class UpdateProgressForSetResponse(val response: StatusResponse, val msg: String)

// Util Formats
data class SetMetadata(val set_id: Int, val title: String, val type: String, val is_public: Boolean, val likes: Int)
class CardSetEntity() {


    fun createCardSet(set: CreateCardSetEntity) : CreateCardSetResponse {
        val db = DataManager.conn()
        var newSet: SetMetadata? = null
        try {
            val statement = db!!.createStatement()
            val query = """
                INSERT INTO CardSet(creator_id, title, type, is_public_ind, likes)
                VALUES (${set.creator_id}, '${set.title}', '${set.type}', 0, 0);
            """.trimIndent()
            val insertedRows = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS)

            if(insertedRows > 0) {
                val keys = statement.generatedKeys
                keys.next()

                newSet = SetMetadata(
                    set_id =  keys.getInt(1),
                    title = set.title,
                    type = set.type,
                    false,
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

    fun getDataForSet(request: GetDataForSetRequest) : GetDataForSetResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val query = """
                SELECT * FROM CardSet
                WHERE [set_id] = ${request.set_id};
            """.trimIndent()
            val resultSet = statement.executeQuery(query)
            val resultList: MutableList<String> = mutableListOf()
            
            // Since it will only return one row, just extract val of all the columns and return
            // a list of strings.
            if (resultSet.next()) {
                val columnCount = resultSet.metaData.columnCount
                for (i in 1..columnCount) {
                    val columnValue: String = resultSet.getString(i)
                    resultList.add(columnValue)
                }
            }
            return GetDataForSetResponse(StatusResponse.Success, resultList.toList())
        } catch (e: Exception) {
            e.printStackTrace()
            return GetDataForSetResponse(StatusResponse.Failure, listOf(e.message ?: "Unknown error"))
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

    fun getSets() : GetSetsResponse {
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
            return GetSetsResponse(StatusResponse.Success, setList.toList())
        } catch (e: Exception) {
            e.printStackTrace()
            return GetSetsResponse(StatusResponse.Failure, emptyList())
        }
    }

    fun getSetsByUsername(request: GetSetsByUsername) : GetSetsByUsernameResponse {
        val db = DataManager.conn();
        val username = request.username
        try {
            val statement = db!!.createStatement();
            val query = "SELECT c.[set_id], c.title, c.type FROM CardSet c, User u WHERE u.username = '$username' AND u.user_id = c.creator_id"
            val resultSet = statement.executeQuery(query);

            val setList = mutableListOf<SetMetadata>()
            while(resultSet.next()) {
                val set = SetMetadata(
                    set_id = resultSet.getInt("set_id"),
                    title = resultSet.getString("title"),
                    type = resultSet.getString("type"),
                    is_public = resultSet.getInt("is_public_ind") == 1,
                    likes = resultSet.getInt("likes")
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

//    fun getProgressForSet(request: GetProgressForSetRequest) : GetProgressForSetResponse {
//        val db = DataManager.conn();
//        val set_id = request.set_id;
//        var progress: Int = 0;
//        try {
//            val stmt = db!!.createStatement();
//            val query = "SELECT progress FROM CardSet WHERE [set_id] = $set_id";
//            val result = stmt.executeQuery(query);
//            if (result.next()) {
//                progress = result.getInt("progress")
//            }
//            return GetProgressForSetResponse(StatusResponse.Success, progress)
//        } catch (e: Exception) {
//            return GetProgressForSetResponse(StatusResponse.Failure, 0)
//        }
//    }

//    fun updateProgressForSet(request: UpdateProgressForSetRequest) : UpdateProgressForSetResponse {
//        val db = DataManager.conn()
//        val set_id = request.set_id
//        val progress = request.progress
//
//        try {
//            val stmt = db!!.createStatement()
//            val query = "UPDATE CardSet SET progress = $progress WHERE [set_id] = $set_id"
//            stmt.executeUpdate(query);
//        } catch (e: Exception) {
//            val err: String = e.message ?: "Unknown error when updating progress for set $set_id"
//            return UpdateProgressForSetResponse(StatusResponse.Failure, err)
//        }
//        return UpdateProgressForSetResponse(StatusResponse.Success, "")
//    }
}