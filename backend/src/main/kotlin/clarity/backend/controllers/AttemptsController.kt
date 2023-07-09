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
    private val attemptEntity = AttemptsEntity();

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
    @GetMapping("/getAttemptAverage")
    fun getAttemptAverage(@RequestParam user: Int): ResponseEntity<GetUserAverageAttemptsResponse> {
        val averagesResponse = attemptEntity.getUserAverages(user)

        return if(averagesResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(averagesResponse)
        } else {
            ResponseEntity.badRequest().body(averagesResponse)
        }
    }

    // get all of a single user's attempts
    @GetMapping("/getUserAttempts")
    fun getUserAttempts(@RequestParam user: Int): ResponseEntity<GetUserAttemptsResponse> {
        val attemptsResponse = attemptEntity.getUserAttempts(user);

        return if(attemptsResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(attemptsResponse)
        } else {
            ResponseEntity.badRequest().body(attemptsResponse)
        }
    }

    // get all of a single user's attempts for a specific set
    @PostMapping("/getUserAttemptsForSet")
    fun getUserAttemptsForSet(@RequestBody request: GetAttemptsForSetEntity): ResponseEntity<GetAttemptsForSetResponse> {
        val attemptsResponse = attemptEntity.getAttemptsForSet(request);

        return if(attemptsResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(attemptsResponse)
        } else {
            ResponseEntity.badRequest().body(attemptsResponse)
        }
    }
}