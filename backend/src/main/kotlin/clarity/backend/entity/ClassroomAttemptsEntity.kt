package clarity.backend.entity

import clarity.backend.DataManager
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class CreateClassroomAttemptEntity(val task_id: Int, val user_id: Int, val card_id: Int, val audio: MultipartFile)
data class CreateClassroomAttemptResponse(val response: StatusResponse, val metadata: ClassroomAttemptMetadata?, val message: String)

data class ClassroomAttemptMetadata(
    val task_id: Int, val user_id: Int, val card_id: Int, val mispronunciations: List<String>,
    val omissions: List<String>, val insertions: List<String>, val pronunciationScore: Int, val accuracyScore: Int,
    val fluencyScore: Int, val completenessScore: Int)


class ClassroomAttemptsEntity {
    private val db = DataManager.conn();

    fun createClassroomAttempts(attempt: CreateClassroomAttemptEntity): CreateClassroomAttemptResponse {
        val statement = db!!.createStatement()

        try {
            val (task_id, user_id, card_id, _) = attempt;

            val card = CardEntity().getCard(card_id)

            /*
            HERE, we call the Microsoft API with the phrase, and the audio recording
            Assume it gets called here, and we retrieve the following four attributes.
             */

            val pronunciationScore: Int = 91
            val accuracyScore: Int = 92
            val fluencyScore: Int = 22
            val completenessScore: Int = 80

            val attemptMetadata = ClassroomAttemptMetadata(task_id, user_id, card_id, listOf(), listOf(), listOf(),
                pronunciationScore, accuracyScore, fluencyScore, completenessScore)

            val currentDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)

            val insertClassroomAttempt =
                "INSERT INTO ClassroomAttempts " +
                "(task_id, user_id, card_id, pronunciation, accuracy, fluency, completeness, attempt_date) VALUES " +
                "($task_id, $user_id, $card_id, $pronunciationScore, $accuracyScore, $fluencyScore, $completenessScore, '$currentDate')"

            val insertedAttempts = statement.executeUpdate(insertClassroomAttempt)

            return if(insertedAttempts > 0) {
                CreateClassroomAttemptResponse(StatusResponse.Success, attemptMetadata, message = "Attempt recorded successfully")
            } else {
                CreateClassroomAttemptResponse(StatusResponse.Failure, attemptMetadata, message = "Could not record attempt ")
            }
        } catch (e: Exception) {
            e.printStackTrace();
            return CreateClassroomAttemptResponse(StatusResponse.Failure, null, message = "Unknown error: ${e.message}")
        }
    }

    fun getTaskScores(task_id: Int) {
        val statement = db!!.createStatement()

        try {

        } catch (e: Exception) {
            e.printStackTrace()

        }

    }
}