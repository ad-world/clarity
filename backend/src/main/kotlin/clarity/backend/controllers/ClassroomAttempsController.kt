package clarity.backend.controllers

import clarity.backend.entity.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/classroom")
class ClassroomAttemptsController {
    private val classroomAttemptsEntity = ClassroomAttemptsEntity()

    @PostMapping("/attemptCard")
    fun attemptCard(@RequestBody attempt: CreateClassroomAttemptEntity): ResponseEntity<CreateClassroomAttemptResponse> {
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
}