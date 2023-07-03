package clarity.backend.entity

import clarity.backend.DataManager
import java.lang.Exception
import javax.xml.crypto.Data

// Request Formats.
data class CreateCardSetEntity(val creator_id: Int, val title: String, val type: String)
data class AddCardToSetRequest(val card_id: Int, val set_id: Int)
data class DeleteCardFromSetRequest(val card_id: Int, val set_id: Int)
data class GetCardsInSetRequest(val set_id: Int)
data class GetDataForSetRequest(val set_id: Int)
data class GetSetsByUsername(val username: String)

// Response Formats.
data class CreateCardSetResponse(val response: StatusResponse, val msg: String)
data class AddCardToSetResponse(val response: StatusResponse, val msg: String)
data class DeleteCardFromSetResponse(val response: StatusResponse, val msg: String)
data class GetCardsInSetResponse(val response: StatusResponse, val cards: List<String>)
data class GetSetsResponse(val response: StatusResponse, val sets: List<String>)
data class GetDataForSetResponse(val response: StatusResponse, val data: List<String>)
data class GetSetsByUsernameResponse(val response: StatusResponse, val data: List<SetMetadata>)

// Util Formats
data class SetMetadata(val set_id: Int, val title: String, val type: String)
class CardSetEntity() {


    fun createCardSet(set: CreateCardSetEntity) : CreateCardSetResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val query = """
                INSERT INTO CardSet(creator_id, title, type)
                VALUES (${set.creator_id}, '${set.title}', '${set.type}');
            """.trimIndent()
            statement.executeUpdate(query)
        } catch (e: Exception) {
            val errMsg: String = "Failed to create card set: ${e.message ?: "Unknown error"}"
            return CreateCardSetResponse(StatusResponse.Failure, errMsg)
        }
        return CreateCardSetResponse(StatusResponse.Success, "Created Card Set!")
    }

    fun addCardToSet(card: AddCardToSetRequest) : AddCardToSetResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val query = """
                INSERT INTO CardInSet([set_id], card_id)
                VALUES (${card.set_id}, ${card.card_id});
            """.trimIndent()
            statement.executeUpdate(query)
        } catch (e: Exception) {
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
            return GetDataForSetResponse(StatusResponse.Failure, listOf(e.message) as List<String>)
        }
    }

    fun getTotalCardsFromSet(set: GetCardsInSetRequest) : GetCardsInSetResponse {
        val db = DataManager.conn()

        try {
            val statement = db!!.createStatement()
            val query = """
                SELECT card_id FROM CardInSet
                WHERE [set_id] = ${set.set_id}
            """.trimIndent()
            val resultSet = statement.executeQuery(query)
            val cardIdList = mutableListOf<String>()

            // Extract the card ids and convert to a list of strings.
            while (resultSet.next()) {
                val cardId = resultSet.getString("card_id")
                cardIdList.add(cardId)
            }
            resultSet.close()

            return GetCardsInSetResponse(StatusResponse.Success, cardIdList.toList())

        } catch (e: Exception) {

            val errMsg: String = "Failed to get cards: ${e.message ?: "Unknown error"}"
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
            return GetSetsResponse(StatusResponse.Failure, emptyList())
        }
    }

    fun getSetsByUsername(request: GetSetsByUsername): GetSetsByUsernameResponse {
        val db = DataManager.conn()!!;
        val username = request.username
        try {
            val statement = db.createStatement();
            val query = "SELECT c.[set_id], c.title, c.type FROM CardSet c, User u WHERE u.username == '$username' AND u.user_id = c.set_id"
            val resultSet = statement.executeQuery(query);

            val setList = mutableListOf<SetMetadata>()
            while(resultSet.next()) {
                val set = SetMetadata(set_id = resultSet.getInt("set_id"), title = resultSet.getString("title"), type = resultSet.getString("type"))
                setList.add(set)
            }
            resultSet.close()

            return GetSetsByUsernameResponse(StatusResponse.Success, setList)
        } catch (e: Exception) {
            return GetSetsByUsernameResponse(StatusResponse.Failure, emptyList())
        }
    }
}