package clarity.backend.controllers

import SpeechAPIResponse
import clarity.backend.entity.*
import clarity.backend.util.SpeechAnalysis
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class AttemptsController {
    // record an attempt
    private val attemptEntity = AttemptsEntity();

    @PostMapping("/testEndpoint")
    fun testEndpoint(@RequestParam("file") file: MultipartFile, @RequestParam body: String): ResponseEntity<SpeechAPIResponse> {

        // This is how speech works rn, will make it better later.
        // If you wanna try it out, record yourself speaking some phrase. Make sure it's a .wav file.
        // Send the file and the phrase in the post request, and you'll get the result

        val analysis = SpeechAnalysis().analyzeAudio(file, body)
        return ResponseEntity.ok(analysis?.json)
    }

    @PostMapping("/attemptCard")
    fun attemptCard(@RequestParam("user_id") user_id: Int, @RequestParam("card_id") card_id: Int, @RequestParam("set_id") set_id: Int, @RequestParam("audio") audio: MultipartFile): ResponseEntity<CreateAttemptResponse> {
        val attempt = CreateAttemptEntity(
            user_id = user_id,
            set_id = set_id,
            card_id = card_id,
            audio = audio
        )

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

    @PostMapping("/practiceAttemptCard")
    fun practiceAttemptCard(@RequestParam("user_id") user_id: Int, @RequestParam("card_id") card_id: Int, @RequestParam("set_id") set_id: Int, @RequestParam("audio") audio: MultipartFile): ResponseEntity<PracticeAttemptResponse> {
        val request = PracticeAttemptEntity(
            user_id = user_id,
            set_id = set_id,
            card_id = card_id,
            audio = audio
        )
        val practiceResponse = attemptEntity.practiceAttemptCard(request)

        return if(practiceResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(practiceResponse)
        } else {
            ResponseEntity.badRequest().body(practiceResponse)
        }
    }
}