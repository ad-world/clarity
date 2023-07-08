package clarity.backend.entity

import clarity.backend.DataManager
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class CreateAttemptEntity(val set_id: Int, val user_id: Int, val card_id: Int, val audio: MultipartFile)
data class CreateAttemptResponse(val response: StatusResponse, val metadata: AttemptMetadata?, val message: String)
data class AttemptMetadata(
    val set_id: Int, val user_id: Int, val card_id: Int, val mispronunciations: List<String>,
    val omissions: List<String>, val insertions: List<String>, val pronunciationScore: Int, val accuracyScore: Int,
    val fluencyScore: Int, val completenessScore: Int)

data class GetUserAverageAttemptsRequest(val user_id: Int)
data class GetUserAverageAttemptsResponse(val response: StatusResponse, val user_id: Int, val pronunciationScore: Double? = null, val accuracyScore: Double? = null,
                                          val fluencyScore: Double? = null, val completenessScore: Double? = null, val message: String)


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
            val completenessScore: Int = 100

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

    fun getUserAverages(request: GetUserAverageAttemptsRequest): GetUserAverageAttemptsResponse {
        val statement = db!!.createStatement();
        try {
            val user = request.user_id
            val selectAveragesQuery = "SELECT AVG(pronunciation), AVG(accuracy), AVG(fluency), AVG(completeness) FROM Attempts WHERE user_id = $user"
            val averagesResult = statement.executeQuery(selectAveragesQuery);

            return if(averagesResult.next()) {
                GetUserAverageAttemptsResponse(
                    response = StatusResponse.Success,
                    user_id = user,
                    pronunciationScore = averagesResult.getDouble(0),
                    accuracyScore = averagesResult.getDouble(1),
                    fluencyScore = averagesResult.getDouble(2,),
                    completenessScore = averagesResult.getDouble(3),
                    message = "Averages found successfully."
                )
            } else {
                GetUserAverageAttemptsResponse(
                    response = StatusResponse.Failure,
                    user_id = user,
                    message = "Averages could not be found for user $user"
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
            val errMsg = "Failed to retrieve averages: ${e.message ?: "Unknown error"}"
            return GetUserAverageAttemptsResponse(StatusResponse.Failure, user_id = request.user_id, message = errMsg)
        }
    }
}