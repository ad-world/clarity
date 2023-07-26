package clarity.backend.entity

import clarity.backend.DataManager
import clarity.backend.util.Difficulty
import java.lang.StringBuilder
import java.sql.Statement
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

enum class StatusResponse {
    Success,
    Failure
}

data class UserWithId(val user_id: Int, val username: String, val email: String, val firstname: String, val lastname: String, val phone_number: String, val login_streak: Int, val difficulty: Difficulty, val enableNotifications: Int)
data class UserLoginEntity(val username: String, val password: String)
data class CreateUserEntity(val username: String, val email: String, val password: String, val firstname: String, val lastname: String, val phone_number: String, val difficulty: Difficulty)
data class EditUserEntity(val user_id: Int, val firstname: String? = null, val lastname: String? = null, val email: String? = null, val enableNotifications: Int? = null)
data class ChangePasswordEntity(val user_id: Int, val old_password: String, val new_password: String)
data class UpdateDifficultyEntity(val userId: Int, val newDifficulty: Difficulty? = null)
data class CreateUserResponse(val response: StatusResponse, val message: String, val userId: Int?, val username: String?)
data class GetUserResponse(val response: StatusResponse, val user: UserWithId?, val message: String)
data class GetAllUsersResponse(val response: StatusResponse, val users: List<UserWithId>)
data class LoginResponse(val response: StatusResponse, val message: String, val user: UserWithId?)
data class UpdateDifficultyResponse(val response: StatusResponse, val newDifficulty: Difficulty?, val message: String)
data class EditUserResponse(val response: StatusResponse, val message: String)
data class ChangePasswordResponse(val response: StatusResponse, val message: String)

class UserEntity() {
    fun checkCredentials(user: UserLoginEntity): UserWithId? {
        val db = DataManager.conn()

        try {
            val statement = db!!.createStatement()
            val selectStatement = "SELECT * FROM User WHERE username = '${user.username}' AND password = '${user.password}'";
            val result = statement.executeQuery(selectStatement);
            if(result.next()) {
                val lastLoggedIn = result.getString("last_logged_in")
                val loginStreak = result.getInt("login_streak")
                val currentDate = LocalDate.now()

                // Check difference between last login date and today
                val newLoginStreak = when (ChronoUnit.DAYS.between(currentDate, LocalDate.parse(lastLoggedIn, DateTimeFormatter.ISO_DATE)).toInt()) {
                    0 -> loginStreak
                    1 -> loginStreak + 1
                    else -> 1
                }

                val newUser = UserWithId(
                    username = user.username,
                    email = result.getString("email"),
                    firstname = result.getString("first_name"),
                    lastname = result.getString("last_name"),
                    user_id = result.getInt("user_id"),
                    phone_number = result.getString("phone_number"),
                    login_streak = newLoginStreak,
                    difficulty = Difficulty.values()[result.getInt("difficulty")],
                    enableNotifications = result.getInt("enable_notifications")
                )

                // Updating last logged in + streak
                val updateLastLoggedIn = "UPDATE User SET last_logged_in = '${currentDate.format(DateTimeFormatter.ISO_DATE)}', login_streak = '${newLoginStreak}' WHERE username = '${user.username}'"
                statement.executeUpdate(updateLastLoggedIn);

                return newUser
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace();
            return null
        }
    }

    fun createUser(user: CreateUserEntity): CreateUserResponse {
        val db = DataManager.conn()

        try {
            val statement = db!!.createStatement()
            val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
            val insertStatement = """
                INSERT INTO User (username, first_name, last_name, email, password, phone_number, last_logged_in, login_streak, difficulty, enable_notifications)
                VALUES(
                '${user.username}', '${user.firstname}', '${user.lastname}', 
                '${user.email}', '${user.password}', '${user.phone_number}',
                '$currentDate', 1, ${Difficulty.Easy.ordinal}, 1
                )
            """.trimIndent()
            val result = statement.executeUpdate(insertStatement, Statement.RETURN_GENERATED_KEYS)
            return if(result > 0) {
                val resultSet = statement.generatedKeys
                resultSet.next()

                val newId = resultSet.getInt(1)
                CreateUserResponse(StatusResponse.Success, "User created successfully", newId, user.username);
            } else {
                CreateUserResponse(StatusResponse.Failure, "Could not create user for unknown reason", null, null)
            }
        } catch(e: Exception) {
            e.printStackTrace();
            return CreateUserResponse(StatusResponse.Failure, e.message ?: "Unknown error", null, null)
        }
    }

    fun getUser(username: String): GetUserResponse {
        val db = DataManager.conn()

        try {
            val selectStatement = "SELECT * FROM User WHERE username = '$username'"
            val statement = db!!.createStatement();
            val result = statement.executeQuery(selectStatement)

            return if(result.next()) {
                val user = UserWithId(
                    username = result.getString("username"),
                    user_id = result.getInt("user_id"),
                    firstname = result.getString("first_name"),
                    lastname = result.getString("last_name"),
                    email = result.getString("email"),
                    phone_number = result.getString("phone_number"),
                    login_streak = result.getInt("login_streak"),
                    difficulty = Difficulty.values()[result.getInt("difficulty")],
                    enableNotifications = result.getInt("enable_notifications")
                )
                GetUserResponse(StatusResponse.Success, user, "User found successfully")
            } else {
                GetUserResponse(StatusResponse.Failure, null, "Unable to find user with username $username");
            }
        } catch (e: Exception) {
            return GetUserResponse(StatusResponse.Failure, null, "Unknown error: ${e.message}")
        }
    }

    fun getAllUsers(): GetAllUsersResponse {
        val db = DataManager.conn()
        try {
            val query = "SELECT * FROM User;"
            val statement = db!!.createStatement()
            val result = statement.executeQuery(query)
            val users = mutableListOf<UserWithId>()
            while (result.next()) {
                val user = UserWithId(
                    username = result.getString("username"),
                    user_id = result.getInt("user_id"),
                    firstname = result.getString("first_name"),
                    lastname = result.getString("last_name"),
                    email = result.getString("email"),
                    phone_number = result.getString("phone_number"),
                    login_streak = result.getInt("login_streak"),
                    difficulty = Difficulty.values()[result.getInt("difficulty")],
                    enableNotifications = result.getInt("enable_notifications")
                )
                users.add(user)
            }
            result.close()
            return GetAllUsersResponse(StatusResponse.Success, users)
        } catch (e: Exception) {
            e.printStackTrace()
            return GetAllUsersResponse(StatusResponse.Failure, emptyList())
        }
    }


    fun getUserById(userId: String): GetUserResponse {
        val db = DataManager.conn()

        try {
            val selectStatement = "SELECT * FROM User WHERE user_id = '$userId'"
            val statement = db!!.createStatement();
            val result = statement.executeQuery(selectStatement)

            return if(result.next()) {
                val user = UserWithId(
                    username = result.getString("username"),
                    user_id = result.getInt("user_id"),
                    firstname = result.getString("first_name"),
                    lastname = result.getString("last_name"),
                    email = result.getString("email"),
                    phone_number = result.getString("phone_number"),
                    login_streak = result.getInt("login_streak"),
                    difficulty = Difficulty.values()[result.getInt("difficulty")],
                    enableNotifications = result.getInt("enable_notifications")
                )
                GetUserResponse(StatusResponse.Success, user, "User found successfully")
            } else {
                GetUserResponse(StatusResponse.Failure, null, "Unable to find user with username $userId");
            }
        } catch (e: Exception) {
            return GetUserResponse(StatusResponse.Failure, null, "Unknown error: ${e.message}")
        }
    }

    fun updateDifficulty(request: UpdateDifficultyEntity): UpdateDifficultyResponse {
        val db = DataManager.conn()

        try {
            val statement = db!!.createStatement()
            val updateStatement = """
                UPDATE User SET difficulty = ${request.newDifficulty?.ordinal} WHERE user_id = ${request.userId}
            """.trimIndent()
            val result = statement.executeUpdate(updateStatement)

            return if(result > 0) {
                UpdateDifficultyResponse(
                    StatusResponse.Success,
                    request.newDifficulty,
                    "Difficulty updated successfully"
                )
            } else {
                UpdateDifficultyResponse(
                    StatusResponse.Success,
                    null,
                    "Could not update difficulty."
                )
            }

        } catch (e: Exception) {
            return UpdateDifficultyResponse(
                StatusResponse.Failure,
                newDifficulty = request.newDifficulty,
                message = e.message ?: "Unknown error"
            )
        }
    }

    fun editUser(request: EditUserEntity): EditUserResponse {
        val db = DataManager.conn()

        try {
            val statement = db!!.createStatement()
            val updates = mutableListOf<String>()

            if(request.email != null) {
                updates.add("email = '${request.email}'")
            }

            if(request.firstname != null) {
                updates.add("first_name = '${request.firstname}'")
            }

            if(request.lastname != null) {
                updates.add("last_name = '${request.lastname}'")
            }

            if(request.enableNotifications != null) {
                updates.add("enable_notifications = ${request.enableNotifications}")
            }

            return if(updates.isNotEmpty()) {
                val updateQuery: StringBuilder = StringBuilder("UPDATE User SET ")
                    .append(updates.joinToString(","))
                    .append(" WHERE user_id = ${request.user_id}")
                val rows = statement.executeUpdate(updateQuery.toString())
                EditUserResponse(
                    StatusResponse.Success,
                    "User updated successfully"
                )
            } else {
                EditUserResponse(
                    StatusResponse.Failure,
                    "No updates were found."
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return EditUserResponse(
                StatusResponse.Failure,
                e.message ?: "Unknown error"
            )
        }
    }

    fun changePassword(request: ChangePasswordEntity): ChangePasswordResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val getUserQuery = "SELECT * FROM User where user_id = ${request.user_id}"
            val user = statement.executeQuery(getUserQuery)
            if(user.next()) {
                val currentPassword = user.getString("password")

                if(currentPassword != request.old_password) {
                    throw Exception("Old password does not match our records.")
                }

                val updateQuery = "UPDATE User SET password = '${request.new_password}' WHERE user_id = ${request.user_id}"
                statement.executeUpdate(updateQuery)

                return ChangePasswordResponse(
                    StatusResponse.Success,
                    "Password updated successfully"
                )

            } else {
                return ChangePasswordResponse(
                    StatusResponse.Failure,
                    "User not found."
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ChangePasswordResponse(
                StatusResponse.Failure,
                e.message ?: "Unknown error"
            )
        }
    }
}