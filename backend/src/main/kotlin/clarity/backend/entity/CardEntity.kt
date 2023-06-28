package clarity.backend.entity

import clarity.backend.DataManager
import kotlin.random.Random
import java.lang.Exception

// Request Formats.
data class CreateCardEntity(val phrase: String, val title: String)
data class Evaluate(val user_recording: String) // Just wrote it as string for now.

// Response Formats.
data class CreateCardResponse(val response: StatusResponse, val msg: String)
data class EvaluateResponse(val response: StatusResponse, val score: Int)


class CardEntity() {

    fun createCard(card: CreateCardEntity) : CreateCardResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val query = """
                INSERT OR IGNORE INTO Card(phrase, title)
                VALUES ('${card.phrase}', '${card.title}');
            """.trimIndent()
            statement.executeUpdate(query)
        } catch (e: Exception) {
            val errMsg: String = "Failed to create card: ${e.message ?: "Unknown error"}"
            return CreateCardResponse(StatusResponse.Failure, errMsg)
        }
        return CreateCardResponse(StatusResponse.Success, "Successfully created a new card.")
    }

    fun evaluate(eval: Evaluate) : EvaluateResponse {
        //TODO: Implement the API call for the ML model. For now, return a random value bet. 80-100.
        return EvaluateResponse(StatusResponse.Success, Random.nextInt(80, 101))
    }

}