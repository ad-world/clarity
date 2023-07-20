package clarity.backend.entity

import clarity.backend.DataManager
import org.yaml.snakeyaml.error.Mark

data class CreateNotification(val userId: Int, val message: String, val notificationDate: String)
data class MarkMessage(val notificationId: Int, val isRead: Int)
data class Notification(val notificationId: Int, val userId: Int, val message: String, val notificationDate: String, val messageRead: Int)
data class GetUnreadResponse(val response: StatusResponse, val messages: List<Notification>)
data class NotificationResponse(val response: StatusResponse, val message: String)


class InboxEntity {

    fun getUnread(userId: Int) : GetUnreadResponse {
        try {
            val db = DataManager.conn()
            val statement = db!!.createStatement()
            val selectStatement = """
                SELECT * FROM Inbox WHERE user_id=${userId} AND message_read=${0}
            """.trimIndent()
            val messages = mutableListOf<Notification>()
            val result = statement.executeQuery(selectStatement)
            while (result.next()) {
                messages.add(
                    Notification(
                    result.getString("notification_id").toInt(),
                    result.getString("user_id").toInt(),
                    result.getString("message"),
                    result.getString("notification_date"),
                    result.getString("message_read").toInt()
                ))
            }
            return GetUnreadResponse(StatusResponse.Success, messages)
        } catch(e: Exception) {
            e.printStackTrace();
            val resultPlaceholder = mutableListOf<Notification>()
            return GetUnreadResponse(StatusResponse.Failure, resultPlaceholder)
        }
    }

    fun markMessage(notification: MarkMessage) : NotificationResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val insertStatement = """
                UPDATE Inbox SET message_read=${notification.isRead} 
                WHERE notification_id=${notification.notificationId}
            """.trimIndent()
            val result = statement.executeUpdate(insertStatement)
            return NotificationResponse(StatusResponse.Success, "Marked message")
        } catch(e: Exception) {
            e.printStackTrace();
            return NotificationResponse(StatusResponse.Failure, "Could not mark message")
        }
    }

    fun deleteMessage(notificationId: Int) : NotificationResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val deleteStatement = """
                DELETE FROM Inbox WHERE notification_id=${notificationId}
            """.trimIndent()
            val result = statement.executeUpdate(deleteStatement)
            return NotificationResponse(StatusResponse.Success, "Deleted Message Successfully")
        } catch(e: Exception) {
            e.printStackTrace();
            return NotificationResponse(StatusResponse.Failure, "Could not delete message")
        }
    }

    fun createNotification(notification: CreateNotification) : NotificationResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val insertStatement = """
                INSERT INTO Inbox (user_id, message, notification_date, message_read)
                VALUES(
                ${notification.userId}, '${notification.message}', '${notification.notificationDate}', ${0}
                )
            """.trimIndent()
            val result = statement.executeUpdate(insertStatement)
            return NotificationResponse(StatusResponse.Success, "Created Notification Successfully")
        } catch(e: Exception) {
            e.printStackTrace();
            return NotificationResponse(StatusResponse.Failure, "Could not create notification")
        }
    }
}