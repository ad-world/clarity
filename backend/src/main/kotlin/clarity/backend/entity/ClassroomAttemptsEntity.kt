package clarity.backend.entity

import SpeechAPIResponse
import clarity.backend.DataManager
import clarity.backend.controllers.SpeechAnalysis
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class CreateClassroomAttemptEntity(val task_id: Int, val user_id: Int, val card_id: Int, val audio: MultipartFile)
data class CreateClassroomAttemptResponse(val response: StatusResponse, val metadata: ClassroomAttemptMetadata?, val message: String)

data class ClassroomAttemptMetadata(
    val task_id: Int, val user_id: Int, val card_id: Int, val mispronunciations: List<String>,
    val omissions: List<String>, val insertions: List<String>, val pronunciationScore: Double, val accuracyScore: Double,
    val fluencyScore: Double, val completenessScore: Double, val speechAPIResponse: SpeechAPIResponse)

data class TaskAttemptWithName(val task_id: Int, val user_id: Int, val card_id: Int, val pronunciationScore: Int,
                        val accuracyScore: Int, val fluencyScore: Int, val completenessScore: Int, val attempt_date: String,
                        val firstName: String, val lastName: String)
data class TaskAttemptWithNameAndClass(val classroom: String, val task_id: Int, val user_id: Int, val card_id: Int, val pronunciationScore: Int,
                               val accuracyScore: Int, val fluencyScore: Int, val completenessScore: Int, val attempt_date: String,
                               val firstName: String, val lastName: String)
data class GetTaskAttemptsResponse(val task_id: Int, val attempts: List<TaskAttemptWithName>, val response: StatusResponse)
data class GetClassAttemptsResponse(val classroom: String, val attempts: List<TaskAttemptWithNameAndClass>, val response: StatusResponse)

class ClassroomAttemptsEntity {
    private val db = DataManager.conn();

    fun createClassroomAttempts(attempt: CreateClassroomAttemptEntity): CreateClassroomAttemptResponse {
        val statement = db!!.createStatement()
        val speechAnalyzer = SpeechAnalysis()


        try {
            val (task_id, user_id, card_id, audio) = attempt;

            val card = CardEntity().getCard(card_id)

            val analysis = card?.let { speechAnalyzer.analyzeAudio(audio, it.phrase) }
                ?: throw Exception("Speech analysis returned null - unknown error")

            val json = analysis.json
            val result = analysis.assessmentResult ?: throw Exception("Speech analysis result was null - don't record this attempt")

            val pronunciationScore = result.pronunciationScore
            val fluencyScore = result.fluencyScore
            val accuracyScore = result.accuracyScore
            val completenessScore = result.completenessScore

            val attemptMetadata = ClassroomAttemptMetadata(task_id, user_id, card_id, listOf(), listOf(), listOf(),
                pronunciationScore, accuracyScore, fluencyScore, completenessScore, json)

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

    fun getTaskAttempts(task: Int): GetTaskAttemptsResponse {
        val statement = db!!.createStatement()

        try {
            val selectQuery = """
                SELECT ClassroomAttempts.*, User.first_name, User.last_name
                FROM ClassroomAttempts
                JOIN User ON ClassroomAttempts.user_id = User.user_id
                WHERE ClassroomAttempts.task_id = $task
                    """.trimIndent()
            val taskList = mutableListOf<TaskAttemptWithName>()

            val taskResults = statement.executeQuery(selectQuery);
            while (taskResults.next()) {
                taskList.add(
                    TaskAttemptWithName(
                        task_id = task,
                        user_id = taskResults.getInt("user_id"),
                        card_id = taskResults.getInt("card_id"),
                        pronunciationScore = taskResults.getInt("pronunciation"),
                        accuracyScore = taskResults.getInt("accuracy"),
                        fluencyScore = taskResults.getInt("fluency"),
                        completenessScore = taskResults.getInt("completeness"),
                        attempt_date = taskResults.getString("attempt_date"),
                        firstName = taskResults.getString("first_name"),
                        lastName = taskResults.getString("last_name")
                    )
                )
            }

            return GetTaskAttemptsResponse(
                response = StatusResponse.Success,
                attempts = taskList,
                task_id = task
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return GetTaskAttemptsResponse(
                response = StatusResponse.Failure,
                attempts = listOf(),
                task_id = task
            )
        }
    }

    fun getClassAttempts(classroom: String): GetClassAttemptsResponse {
        val statement = db!!.createStatement()

        try {
            val selectQuery = """
                SELECT ClassroomAttempts.*, User.first_name, User.last_name
                FROM ClassroomAttempts
                JOIN User ON ClassroomAttempts.user_id = User.user_id
                JOIN Tasks T on ClassroomAttempts.task_id = T.task_id
                WHERE T.class_id = $classroom
                    """.trimIndent()
            val taskList = mutableListOf<TaskAttemptWithNameAndClass>()

            val taskResults = statement.executeQuery(selectQuery);
            while (taskResults.next()) {
                taskList.add(
                    TaskAttemptWithNameAndClass(
                        task_id = taskResults.getInt("task_id"),
                        user_id = taskResults.getInt("user_id"),
                        card_id = taskResults.getInt("card_id"),
                        pronunciationScore = taskResults.getInt("pronunciation"),
                        accuracyScore = taskResults.getInt("accuracy"),
                        fluencyScore = taskResults.getInt("fluency"),
                        completenessScore = taskResults.getInt("completeness"),
                        attempt_date = taskResults.getString("attempt_date"),
                        firstName = taskResults.getString("first_name"),
                        lastName = taskResults.getString("last_name"),
                        classroom = classroom
                    )
                )
            }

            return GetClassAttemptsResponse(
                response = StatusResponse.Success,
                attempts = taskList,
                classroom = classroom
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return GetClassAttemptsResponse(
                response = StatusResponse.Failure,
                attempts = listOf(),
                classroom = classroom
            )
        }
    }
}