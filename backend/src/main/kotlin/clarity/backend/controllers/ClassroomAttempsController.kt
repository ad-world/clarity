package clarity.backend.controllers

import clarity.backend.entity.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/classroom")
class ClassroomAttemptsController {
    private val classroomAttemptsEntity = ClassroomAttemptsEntity()

    @PostMapping("/attemptCard")
    fun attemptCard(@RequestParam("user_id") user_id: Int, @RequestParam("card_id") card_id: Int, @RequestParam("task_id") task_id: Int, @RequestParam("audio") audio: MultipartFile): ResponseEntity<CreateClassroomAttemptResponse> {
        val attempt = CreateClassroomAttemptEntity(
            task_id = task_id,
            user_id = user_id,
            card_id = card_id,
            audio = audio
        )

        val attemptResponse = classroomAttemptsEntity.createClassroomAttempts(attempt);

        return if(attemptResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(attemptResponse)
        } else {
            ResponseEntity.badRequest().body(attemptResponse)
        }
    }

    @GetMapping("/getTaskAttempts")
    fun getTaskAttempts(@RequestParam task: Int): ResponseEntity<GetTaskAttemptsResponse> {
        val attemptResponse = classroomAttemptsEntity.getTaskAttempts(task)

        return if(attemptResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(attemptResponse)
        } else {
            ResponseEntity.badRequest().body(attemptResponse);
        }
    }

    @GetMapping("/getClassAttempts")
    fun getClassAttempts(@RequestParam classroom: String): ResponseEntity<GetClassAttemptsResponse> {
        val attemptResponse = classroomAttemptsEntity.getClassAttempts(classroom)

        return if(attemptResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(attemptResponse)
        } else {
            ResponseEntity.badRequest().body(attemptResponse);
        }
    }

    @GetMapping("/getTaskProgress")
    fun getTaskProgress(@RequestParam task_id: Int): ResponseEntity<GetClassroomTaskProgressResponse> {
        val attemptResponse = classroomAttemptsEntity.getClassroomTaskProgress(GetClassroomTaskProgressRequest(task_id));

        return if(attemptResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(attemptResponse)
        } else {
            ResponseEntity.badRequest().body(attemptResponse)
        }
    }
}