package clarity.backend.util

import clarity.backend.DataManager
import clarity.backend.entity.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class EmailService(
    private val mailSender: JavaMailSender

) {
    @Value("\${spring.mail.username}")
    private val from: String? = null

    private val db = DataManager.conn();

    fun sendEmailList(to: List<String>, subject: String, body: String) {
        if (to.isNotEmpty()) {
            val message = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true)
            if (from != null) {
                helper.setFrom(from)
            }
            helper.setTo(to.toTypedArray())
            helper.setSubject(subject)
            helper.setText(body, true)
            mailSender.send(message)
        }
    }

    fun sendEmailNewAnnouncement(post: CreateAnnouncementEntity) {
        // get all students in the class
        val getUsersStatement = db!!.createStatement()
        val selectStatement = """
                    SELECT user_id FROM ClassroomStudents WHERE class_id='${post.classId}'
                """.trimIndent()
        val resultUsers = getUsersStatement.executeQuery(selectStatement)
        val userIds = mutableListOf<String>()
        while (resultUsers.next()) {
            userIds.add(resultUsers.getString("user_id"))
        }
        val emails = mutableListOf<String>()

        val classname = "SELECT * FROM Classroom WHERE private_code = '${post.classId}'"
        val results = getUsersStatement.executeQuery(classname)
        var className = ""
        if(results.next()) {
            className = results.getString("name")
        }

        for (user in userIds) {
            val getEmailStatement = db.createStatement()
            val selectEmailStatement = """
                    SELECT email FROM User WHERE enable_notifications=1 AND user_id=${user}
                """.trimIndent()
            val resultEmail = getEmailStatement.executeQuery(selectEmailStatement)
            if (resultEmail.next()) {
                emails.add(resultEmail.getString("email"))
            }
            val currDate = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formattedDate = currDate.format(formatter)
            createNotification(
                CreateNotification(
                    user.toInt(),
                    "New Announcement for $className",
                    formattedDate
                )
            )
        }


        val emailBody = "New Notification from $className\n\n\n${post.text}\n\nDescription: ${post.description}"

        sendEmailList(
            emails,
            "New Announcement for $className",
            emailBody
        )
    }

    fun sendEmailNewTask(task: CreateTaskEntity) {
        // get all students in the class
        val getUsersStatement = db!!.createStatement()
        val selectStatement = """
                    SELECT user_id FROM ClassroomStudents WHERE class_id='${task.classId}'
                """.trimIndent()
        val resultUsers = getUsersStatement.executeQuery(selectStatement)
        val userIds = mutableListOf<String>()
        while (resultUsers.next()) {
            userIds.add(resultUsers.getString("user_id"))
        }
        val emails = mutableListOf<String>()

        val classname = "SELECT * FROM Classroom WHERE private_code = '${task.classId}'"
        val results = getUsersStatement.executeQuery(classname)
        var className = ""
        if(results.next()) {
            className = results.getString("name")
        }

        for (user in userIds) {
            val getEmailStatement = db!!.createStatement()
            val selectEmailStatement = """
                    SELECT email FROM User WHERE enable_notifications=${1} AND user_id=${user}
                """.trimIndent()
            val resultEmail = getEmailStatement.executeQuery(selectEmailStatement)
            if (resultEmail.next()) {
                emails.add(resultEmail.getString("email"))
            }
            val currDate = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formattedDate = currDate.format(formatter)
            createNotification(CreateNotification(user.toInt(), "New Task for $className", formattedDate))
        }
        sendEmailList(
            emails,
            "New Task for $className",
            task.name + "\nDescription: " + task.description + "\nDueDate: " + task.dueDate
        )
    }

    fun sendEmailNewFollow(request: FollowingRequestEntity) {
        val getEmailStatement = db!!.createStatement()
        val selectEmailStatement = """
                SELECT email FROM User WHERE enable_notifications=${1} AND user_id=${request.followingId}
            """.trimIndent()
        val resultEmail = getEmailStatement.executeQuery(selectEmailStatement)
        val emails = mutableListOf<String>()
        if (resultEmail.next()) {
            emails.add(resultEmail.getString("email"))
        }
        val currDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currDate.format(formatter)

        val user = UserEntity().getUserById(userId = request.userId.toString())
        createNotification(
            CreateNotification(
                request.followingId,
                user.user?.username ?: "Unknown user",
                formattedDate
            )
        )
        sendEmailList(emails, "New Follower", "${user.user?.username ?: "Unknown user"} started to follow you")
    }

    fun createNotification(notification: CreateNotification) {
        val statement = db!!.createStatement()
        val insertStatement = """
                INSERT INTO Inbox (user_id, message, notification_date, message_read)
                VALUES(
                ${notification.userId}, '${notification.message}', '${notification.notificationDate}', ${0}
                )
            """.trimIndent()
        val result = statement.executeUpdate(insertStatement)
    }
}
