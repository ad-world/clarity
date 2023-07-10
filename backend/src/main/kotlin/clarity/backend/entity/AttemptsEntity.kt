package clarity.backend.entity

import clarity.backend.DataManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class CreateAttemptEntity(val set_id: Int, val user_id: Int, val card_id: Int, val audio: Int)
// audio is Int for now, will change once we figure out what it needs to be
data class CreateAttemptResponse(val response: StatusResponse, val metadata: AttemptMetadata?, val message: String)
data class AttemptMetadata(
    val set_id: Int, val user_id: Int, val card_id: Int, val mispronunciations: List<String>,
    val omissions: List<String>, val insertions: List<String>, val pronunciationScore: Int, val accuracyScore: Int,
    val fluencyScore: Int, val completenessScore: Int)
data class GetUserAverageAttemptsResponse(val response: StatusResponse, val user_id: Int, val pronunciationScore: Double? = null, val accuracyScore: Double? = null,
                                          val fluencyScore: Double? = null, val completenessScore: Double? = null, val message: String)
data class CardAttempt(val user_id: Int, val card_id: Int, val set_id: Int, val pronunciationScore: Double? = null, val accuracyScore: Double? = null,
                       val fluencyScore: Double? = null, val completenessScore: Double? = null, val attemptDate: String)
data class GetUserAttemptsResponse(val user_id: Int, val attempts: List<CardAttempt>, val response: StatusResponse)
data class GetAttemptsForSetEntity(val user: Int, val set: Int)
data class GetAttemptsForSetResponse(val user_id: Int, val set_id: Int, val attempts: List<CardAttempt>, val response: StatusResponse)

class AttemptsEntity {
    private val db = DataManager.conn();

    fun createAttempt(attempt: CreateAttemptEntity): CreateAttemptResponse {
        val statement = db!!.createStatement();
        try {
            val (set_id, user_id, card_id, audio) = attempt;

            val card = CardEntity().getCard(card_id)

            /*
            HERE, we call the Microsoft API with the phrase, and the audio recording
            Assume it gets called here, and we retrieve the following four attributes.
             */

            val pronunciationScore: Int = 92
            val accuracyScore: Int = 93
            val fluencyScore: Int = 82
            val completenessScore: Int = 1

            val attemptMetadata = AttemptMetadata(set_id, user_id, card_id, listOf(), listOf(), listOf(),
                pronunciationScore, accuracyScore, fluencyScore, completenessScore)

            val currentDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

            val insertAttempt =
                "INSERT INTO Attempts " +
                "(user_id, set_id, card_id, pronunciation, accuracy, fluency, completeness, attempt_date) VALUES " +
                "($user_id, $set_id, $card_id, $pronunciationScore, $accuracyScore, $fluencyScore, $completenessScore, '$currentDate')"

            val insertedAttempts = statement.executeUpdate(insertAttempt);

            return if (insertedAttempts > 0) {
                CreateAttemptResponse(StatusResponse.Success, attemptMetadata, message = "Attempt recorded successfully")
            } else {
                CreateAttemptResponse(StatusResponse.Failure, attemptMetadata, message = "Could not record attempt")
            }
        } catch (e :Exception) {
            val errMsg = "Failed to create attempt: ${e.message ?: "Unknown error"}"
            statement.close();
            return CreateAttemptResponse(StatusResponse.Failure, null, errMsg)
        }
    }

    fun getUserAverages(request: Int): GetUserAverageAttemptsResponse {
        val statement = db!!.createStatement();
        try {
            val selectAveragesQuery =
                """
                SELECT AVG(pronunciation) AS average_pronunciation,
                AVG(accuracy) AS average_accuracy,
                AVG(fluency) AS average_fluency,
                AVG(completeness) AS average_completeness
                FROM Attempts
                WHERE user_id = $request
                AND EXISTS (SELECT * FROM User WHERE user_id = $request);
                """.trimIndent()
            val averagesResult = statement.executeQuery(selectAveragesQuery);

            return if(averagesResult.next()) {
                val pronunciationScore: Double? = averagesResult.getObject(1) as? Double
                val accuracyScore = averagesResult.getDouble(2)
                val fluencyScore = averagesResult.getDouble(3)
                val completenessScore = averagesResult.getDouble(4)

                // If pronunciationScore is null - the userId doesn't exist OR they haven't recorded any attemps yet.
                // Nulls will be returned
                if(pronunciationScore != null) {
                    GetUserAverageAttemptsResponse(
                        response = StatusResponse.Success,
                        user_id = request,
                        pronunciationScore,
                        accuracyScore,
                        fluencyScore,
                        completenessScore,
                        message = "Averages found successfully."
                    )
                } else {
                    GetUserAverageAttemptsResponse(
                        response = StatusResponse.Failure,
                        user_id = request,
                        message = "Averages could not be found for user $request"
                    )
                }
            } else {
                GetUserAverageAttemptsResponse(
                    response = StatusResponse.Failure,
                    user_id = request,
                    message = "Averages could not be found for user $request"
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
            val errMsg = "Failed to retrieve averages: ${e.message ?: "Unknown error"}"
            return GetUserAverageAttemptsResponse(StatusResponse.Failure, user_id = request, message = errMsg)
        }
    }

    fun getUserAttempts(user: Int): GetUserAttemptsResponse {
        val statement = db!!.createStatement();

        try {
            val selectQuery = "SELECT * FROM Attempts WHERE user_id = $user"
            val cardAttempts = mutableListOf<CardAttempt>();

            val attemptResults = statement.executeQuery(selectQuery)
            while (attemptResults.next()) {
                cardAttempts.add(
                    CardAttempt(
                        user,
                        set_id = attemptResults.getInt("set_id"),
                        card_id = attemptResults.getInt("card_id"),
                        pronunciationScore = attemptResults.getDouble("pronunciation"),
                        accuracyScore = attemptResults.getDouble("accuracy"),
                        fluencyScore = attemptResults.getDouble("fluency"),
                        completenessScore = attemptResults.getDouble("completeness"),
                        attemptDate = attemptResults.getString("attempt_date")
                    )
                )
            }

            return GetUserAttemptsResponse(
                response = StatusResponse.Success,
                attempts = cardAttempts,
                user_id = user
            )

        } catch (e: Exception) {
            e.printStackTrace();
            return GetUserAttemptsResponse(
                response = StatusResponse.Failure,
                attempts = listOf(),
                user_id = user
            )
        }
    }

    fun getAttemptsForSet(request: GetAttemptsForSetEntity): GetAttemptsForSetResponse {
        val statement = db!!.createStatement()
        val (user, set) = request

        try {
            val selectQuery = "SELECT * FROM Attempts WHERE user_id = $user and set_id = $set"

            val cardAttempt = mutableListOf<CardAttempt>()
            val attemptResult = statement.executeQuery(selectQuery);

            while(attemptResult.next()) {
                cardAttempt.add(
                    CardAttempt(
                        user,
                        set_id = attemptResult.getInt("set_id"),
                        card_id = attemptResult.getInt("card_id"),
                        pronunciationScore = attemptResult.getDouble("pronunciation"),
                        accuracyScore = attemptResult.getDouble("accuracy"),
                        fluencyScore = attemptResult.getDouble("fluency"),
                        completenessScore = attemptResult.getDouble("completeness"),
                        attemptDate = attemptResult.getString("attempt_date")
                    )
                )
            }

            return GetAttemptsForSetResponse(
                user_id = user,
                set_id = set,
                attempts = cardAttempt,
                response = StatusResponse.Success
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return GetAttemptsForSetResponse(
                user_id = user,
                set_id = set,
                attempts = listOf(),
                response = StatusResponse.Failure
            )
        }
    }
}