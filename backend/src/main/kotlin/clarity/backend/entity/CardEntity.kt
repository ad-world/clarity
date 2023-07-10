package clarity.backend.entity

import clarity.backend.DataManager
import kotlin.random.Random
import kotlin.Exception

// Request Formats.
data class CreateCardEntity(val phrase: String, val title: String)

data class PhraseSearchEntity(val phrase: String)
data class Evaluate(val user_recording: String) // Just wrote it as string for now.

// Response Formats.
data class CreateCardResponse(val response: StatusResponse, val msg: String)
data class EvaluateResponse(val response: StatusResponse, val score: Int)

data class PhraseSearchResponse(val response: StatusResponse, val cards: List<Card>)

// Class Formats
data class Card(val card_id: Int, val phrase: String, val title: String)

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

    fun getCard(id: Int): Card? {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val getPhraseSql = "SELECT phrase, title FROM Card WHERE card_id = $id";
            val phraseSet = statement.executeQuery(getPhraseSql);

            if(!phraseSet.next()) {
                throw Exception("Could not find card with id $id")
            }

            val phrase = phraseSet.getString("phrase")
            val title = phraseSet.getString("title")

            return Card(id, phrase, title)
        } catch (e: Exception) {
            e.printStackTrace()
            return null;
        }
    }

    fun phraseSearch(phrase: PhraseSearchEntity): PhraseSearchResponse {
        val db = DataManager.conn();
        try {
            val statement = db!!.createStatement()
            val phraseText = phrase.phrase

            val getPhrasesSql = "SELECT * FROM Card WHERE LOWER(phrase) LIKE LOWER('%$phraseText%') LIMIT 10"
            val phrases  = statement.executeQuery(getPhrasesSql);

            val cards = mutableListOf<Card>()

            while(phrases.next()) {
                cards.add(Card(
                    phrases.getInt("card_id"),
                    phrases.getString("phrase"),
                    phrases.getString("title")
                ))
            }

            return PhraseSearchResponse(StatusResponse.Success, cards);
        } catch (e: Exception) {
            e.printStackTrace();
            return PhraseSearchResponse(StatusResponse.Failure, listOf())
        }
    }


    fun evaluate(eval: Evaluate) : EvaluateResponse {
        //TODO: Implement the API call for the ML model. For now, return a random value bet. 80-100.
        return EvaluateResponse(StatusResponse.Success, Random.nextInt(80, 101))
    }

}