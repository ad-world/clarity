package clarity.backend.controllers

import clarity.backend.util.EmailService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class EmailRequest(val to: String, val subject: String, val body: String)
data class EmailRequestList(val to: List<String>, val subject: String, val body: String)
@RestController
class EmailController(private val emailService: EmailService) {
    @PostMapping("/sendEmail")
    fun sendEmail(@RequestBody emailRequest: EmailRequest) {
        val to = emailRequest.to
        val subject = emailRequest.subject
        val body = emailRequest.body
        emailService.sendEmail(to, subject, body)
    }

    @PostMapping("/sendEmailList")
    fun sendEmail(@RequestBody emailRequest: EmailRequestList) {
        val to = emailRequest.to
        val subject = emailRequest.subject
        val body = emailRequest.body
        emailService.sendEmailList(to, subject, body)
    }
}
