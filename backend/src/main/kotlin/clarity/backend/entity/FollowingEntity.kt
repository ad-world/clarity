package clarity.backend.entity

import clarity.backend.DataManager
import java.io.ObjectInputFilter.Status


data class FollowingRequestEntity(val userId: Int, val followingId: Int)
data class FollowingResponse(val response: StatusResponse, val message: String)
data class FollowerListResponse(val response: StatusResponse, val followers: List<Int>)
class FollowingEntity {

    fun follow(request: FollowingRequestEntity) : FollowingResponse{
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val insertStatement = """
                INSERT INTO Following (following_id, follower_id)
                VALUES(
                ${request.followingId}, ${request.userId}
                )
            """.trimIndent()
            val result = statement.executeUpdate(insertStatement)
            return FollowingResponse(StatusResponse.Success, "Following ${request.followingId}")
        } catch(e: Exception) {
            e.printStackTrace();
            return FollowingResponse(StatusResponse.Failure, "Could not follow ${request.followingId}")
        }
    }

    fun unfollow(request: FollowingRequestEntity) : FollowingResponse{
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val deleteStatement = """
                DELETE FROM Following WHERE following_id=${request.followingId}
                 AND follower_id=${request.userId}
            """.trimIndent()
            val result = statement.executeUpdate(deleteStatement)
            return FollowingResponse(StatusResponse.Success, "Unfollowed ${request.followingId}")
        } catch(e: Exception) {
            e.printStackTrace();
            return FollowingResponse(StatusResponse.Failure, "Could not unfollow ${request.followingId}")
        }
    }

    fun getFollowing(userId: Int) : FollowerListResponse {
        try {
            val db = DataManager.conn()
            val statement = db!!.createStatement()
            val selectStatement = """
                SELECT * FROM Following WHERE follower_id=${userId}
            """.trimIndent()
            val ids = mutableListOf<Int>()
            val result = statement.executeQuery(selectStatement)
            while (result.next()) {
                ids.add(result.getString("following_id").toInt())
            }
            return FollowerListResponse(StatusResponse.Success, ids)
        } catch(e: Exception) {
            e.printStackTrace();
            val resultPlaceholder = mutableListOf<Int>()
            return FollowerListResponse(StatusResponse.Failure, resultPlaceholder)
        }
    }

    fun getFollowers(userId: Int) : FollowerListResponse {
        try {
            val db = DataManager.conn()
            val statement = db!!.createStatement()
            val selectStatement = """
                SELECT * FROM Following WHERE following_id=${userId}
            """.trimIndent()
            val ids = mutableListOf<Int>()
            val result = statement.executeQuery(selectStatement)
            while (result.next()) {
                ids.add(result.getString("follower_id").toInt())
            }
            return FollowerListResponse(StatusResponse.Success, ids)
        } catch(e: Exception) {
            e.printStackTrace();
            val resultPlaceholder = mutableListOf<Int>()
            return FollowerListResponse(StatusResponse.Failure, resultPlaceholder)
        }
    }
}