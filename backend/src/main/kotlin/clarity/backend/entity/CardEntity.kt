package clarity.backend.entity

import clarity.backend.DataManager
import java.sql.Statement
import kotlin.random.Random
import kotlin.Exception

// Request Formats.
data class CreateCardEntity(val phrase: String, val title: String, val setId: Int? = null)

data class PhraseSearchEntity(val phrase: String)
data class Evaluate(val user_recording: String) // Just wrote it as string for now.

// Response Formats.
data class CreateCardResponse(val response: StatusResponse, val msg: String, val card: Card?)
data class EvaluateResponse(val response: StatusResponse, val score: Int)

data class PhraseSearchResponse(val response: StatusResponse, val cards: List<Card>)

// Class Formats
data class Card(val card_id: Int, val phrase: String, val title: String)

class CardEntity() {

    fun createCard(card: CreateCardEntity) : CreateCardResponse {
        var newCard: Card? = null
        val db = DataManager.conn()
        val cardSet = CardSetEntity()

        try {
            val statement = db!!.createStatement()
            val query = """
                INSERT OR IGNORE INTO Card(phrase, title)
                VALUES ('${card.phrase}', '${card.title}');
            """.trimIndent()
            val insertedRows = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS)

            if(insertedRows == 0) {
                // Card already exists, do a search and set newCard = search result
                val searchQuery = """
                    SELECT * FROM Card WHERE phrase = '${card.phrase}'
                """.trimIndent()
                val searchResult = statement.executeQuery(searchQuery)

                if(searchResult.next()) {
                    newCard = Card(
                        card_id = searchResult.getInt("card_id"),
                        phrase = searchResult.getString("phrase"),
                        title = searchResult.getString("title")
                    )
                }
            } else {
                // Card does not exist, new card was created and key was returned
                val keys = statement.generatedKeys
                keys.next()
                newCard = Card(
                    card_id = keys.getInt(1),
                    phrase = card.phrase,
                    title = card.title
                )
            }

            // Add the newCard to the set
            if(card.setId != null && newCard != null) {
                val request = AddCardToSetRequest(card_id = newCard.card_id, set_id = card.setId)
                cardSet.addCardToSet(request)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            val errMsg: String = "Failed to create card: ${e.message ?: "Unknown error"}"
            return CreateCardResponse(StatusResponse.Failure, errMsg, newCard)
        }
        return CreateCardResponse(StatusResponse.Success, "Successfully created / found card.", newCard)
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