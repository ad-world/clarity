package clarity.backend.controllers

import clarity.backend.util.EmailService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class EmailController(private val emailService: EmailService) {

    @PostMapping("/send-email")
    fun sendEmail(@RequestBody emailRequest: EmailRequest) {
        // Assuming you have an EmailRequest class that holds the email details
        val to = emailRequest.to
        val subject = emailRequest.subject
        val body = emailRequest.body

        emailService.sendEmail(to, subject, body)
    }
}

data class EmailRequest(
    val to: String,
    val subject: String,
    val body: String
)
