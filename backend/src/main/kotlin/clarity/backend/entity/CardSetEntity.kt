package clarity.backend.entity

import clarity.backend.DataManager
import java.lang.Exception

// Request Formats.
data class CreateCardSetEntity(val creator_id: Int, val title: String, val type: String)
data class AddCard(val card_id: Int, val set_id: Int)
data class DeleteCard(val card_id: Int, val set_id: Int)

data class GetCardsInSet(val set_id: Int)

// Response Formats.
data class CreateCardSetResponse(val response: StatusResponse, val msg: String)
data class AddCardResponse(val response: StatusResponse, val msg: String)
data class DeleteCardResponse(val response: StatusResponse, val msg: String)
data class GetCardsInSetResponse(val response: StatusResponse, val cards: List<String>)

class CardSetEntity() {
    private val db = DataManager().db

    fun createCardSet(set: CreateCardSetEntity) : CreateCardSetResponse {
        try {
            val statement = db.createStatement()
            val query = """
                INSERT OR IGNORE INTO CardSet(creator_id, title, type)
                VALUES (${set.creator_id}, ${set.title}, ${set.type});
            """.trimIndent()
            statement.executeUpdate(query)
        } catch (e: Exception) {
            val errMsg: String = "Failed to create card set: ${e.message ?: "Unknown error"}"
            return CreateCardSetResponse(StatusResponse.Failure, errMsg)
        }
        return CreateCardSetResponse(StatusResponse.Success, "Created Card Set!")
    }

    fun addCard(card: AddCard) : AddCardResponse {
        try {
            val statement = db.createStatement()
            val query = """
                INSERT OR IGNORE INTO CardInSet([set_id], card_id)
                VALUES (${card.set_id}, ${card.card_id});
            """.trimIndent()
            statement.executeUpdate(query)
        } catch (e: Exception) {
            val errMsg: String = "Failed to add card to set: ${e.message ?: "Unknown error"}"
            return AddCardResponse(StatusResponse.Failure, errMsg)
        }
        return AddCardResponse(StatusResponse.Success, "Added card ${card.card_id} to set.")
    }

    fun deleteCard(card: DeleteCard) : DeleteCardResponse {
        try {
            val statement = db.createStatement()
            val query = """
                DELETE FROM CardInSet
                WHERE [set_id] = ${card.set_id} AND card_id = ${card.card_id};
            """.trimIndent()
            statement.executeUpdate(query)
        } catch (e: Exception) {
            val errMsg: String = "Failed to delete card from set: ${e.message ?: "Unknown error"}"
            return DeleteCardResponse(StatusResponse.Failure, errMsg)
        }
        return DeleteCardResponse(StatusResponse.Success, "Deleted card from set.")
    }

    fun getTotalCardsFromSet(set: GetCardsInSet) : GetCardsInSetResponse {
        try {
            val statement = db.createStatement()
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

}