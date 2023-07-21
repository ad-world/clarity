package clarity.backend.util

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailService @Autowired constructor(
    private val mailSender: JavaMailSender

) {
    @Value("\${spring.mail.username}")
    private val from: String? = null
    fun sendEmail(to: String, subject: String, body: String) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)

        if (from != null) {
            helper.setFrom(from)
        }
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(body, true)

        mailSender.send(message)
    }
}
