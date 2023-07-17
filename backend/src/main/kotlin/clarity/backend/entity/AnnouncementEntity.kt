package clarity.backend.entity

import clarity.backend.DataManager
import java.lang.Exception

data class CreateAnnouncementEntity(val classId: String, val text: String, val description: String, val date: String)

data class Announcement(val announcementId: Int, val classId: String, val text: String, val description: String, val dateCreated: String)

data class AnnouncementResponse(val response: StatusResponse, val message: String)

data class GetAnnouncementsResponse(val response: StatusResponse, val result: List<Announcement>)
class AnnouncementEntity {

    fun createAnnouncement(post: CreateAnnouncementEntity) : AnnouncementResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val insertStatement = """
                INSERT INTO Announcements (class_id, text, description, dateCreated)
                VALUES(
                '${post.classId}', '${post.text}', '${post.description}', '${post.date}'
                )
            """.trimIndent()
            statement.executeUpdate(insertStatement)
            return AnnouncementResponse(StatusResponse.Success, "Successfully created announcement.")
        } catch (e: Exception) {
            val errMsg: String = "Failed to create announcement: ${e.message ?: "Unknown error"}"
            return AnnouncementResponse(StatusResponse.Failure, errMsg)
        }
    }

    fun deleteAnnouncement(id: Int) : AnnouncementResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val deleteStatement = """
                DELETE FROM Announcements WHERE announcement_id ='${id}'
            """.trimIndent()
            statement.executeUpdate(deleteStatement)
            return AnnouncementResponse(StatusResponse.Success, "Successfully deleted announcement.")
        } catch (e: Exception) {
            val errMsg: String = "Failed to delete announcement: ${e.message ?: "Unknown error"}"
            return AnnouncementResponse(StatusResponse.Failure, errMsg)
        }
    }

    fun getAnnouncements(classId: String) : GetAnnouncementsResponse {
        val db = DataManager.conn()
        try {
            val db = DataManager.conn()
            val statement = db!!.createStatement()
            val selectStatement = """
                SELECT * FROM Announcements WHERE class_id='${classId}'
            """.trimIndent()
            val announcementIds = mutableListOf<Announcement>()
            val result = statement.executeQuery(selectStatement)
            while (result.next()) {
                var announcement = Announcement(
                    result.getString("announcement_id").toInt(),
                    result.getString("class_id"),
                    result.getString("text"),
                    result.getString("description"),
                    result.getString("dateCreated")
                )
                announcementIds.add(announcement)
            }
            return GetAnnouncementsResponse(StatusResponse.Success, announcementIds)
        } catch(e: Exception) {
            e.printStackTrace();
            val resultPlaceholder = mutableListOf<Announcement>()
            return GetAnnouncementsResponse(StatusResponse.Failure, resultPlaceholder)
        }
    }
}