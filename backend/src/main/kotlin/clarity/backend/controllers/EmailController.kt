package clarity.backend.controllers

import clarity.backend.util.EmailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class EmailRequest(val to: String, val subject: String, val body: String)
data class EmailRequestList(val to: List<String>, val subject: String, val body: String)
@RestController
class EmailController @Autowired constructor(private val emailService: EmailService) {
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

//    fun sdd() {
//        // get all students in the class
//        val getUsersStatement = db!!.createStatement()
//        val selectStatement = """
//                    SELECT user_id FROM ClassroomStudents WHERE class_id=${post.classId}
//                """.trimIndent()
//        val resultUsers = getUsersStatement.executeQuery(selectStatement)
//        val userIds = mutableListOf<String>()
//        while (resultUsers.next()) {
//            userIds.add(resultUsers.getString("user_id"))
//        }
//
//        val emails = mutableListOf<String>()
//        for (user in userIds) {
//            val getEmailStatement = db!!.createStatement()
//            val selectEmailStatement = """
//                    SELECT email FROM User WHERE enable_notifications=${1} AND user_id=${user}
//                """.trimIndent()
//            val resultEmail = getUsersStatement.executeQuery(selectEmailStatement)
//            if (resultEmail.next()) {
//                emails.add(resultEmail.getString("email"))
//            }
//        }
//        emailService.sendEmailList(emails, "New Announcement", "New Announcement for Class ${post.classId}")
//    }
}
