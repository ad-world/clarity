package clarity.backend.controllers

import clarity.backend.entity.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AttemptsController {
    // record an attempt
    val attemptEntity = AttempsEntity();

    @PostMapping("/attemptCard")
    fun attemptCard(@RequestBody attempt: CreateAttemptEntity): ResponseEntity<CreateAttemptResponse> {
        val attemptResponse = attemptEntity.createAttempt(attempt);

        return if(attemptResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(attemptResponse)
        } else {
            ResponseEntity.badRequest().body(attemptResponse)
        }
    }

    // get user attempt averages
    @PostMapping("/getAttemptAverage")
    fun getAttemptAverage(@RequestBody request: GetUserAverageAttemptsRequest): ResponseEntity<GetUserAverageAttemptsResponse> {
        val averagesResponse = attemptEntity.getUserAverages(request)

        return if(averagesResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(averagesResponse)
        } else {
            ResponseEntity.badRequest().body(averagesResponse)
        }
    }
}