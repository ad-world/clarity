package clarity.backend.entity

import clarity.backend.DataManager
import clarity.backend.util.Difficulty
import java.sql.Statement
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

enum class StatusResponse {
    Success,
    Failure
}

data class User(val username: String, val email: String, val password: String, val firstname: String, val lastname: String, val phone_number: String, val difficulty: Difficulty)
data class UserWithId(val user_id: Int, val username: String, val email: String, val firstname: String, val lastname: String, val phone_number: String, val login_streak: Int, val difficulty: Difficulty)
data class UserLoginEntity(val username: String, val password: String)
data class CreateUserEntity(val user: User)

data class UpdateDifficultyEntity(val userId: Int, val newDifficulty: Difficulty? = null)
data class CreateUserResponse(val response: StatusResponse, val message: String, val userId: Int?, val username: String?)
data class GetUserResponse(val response: StatusResponse, val user: UserWithId?, val message: String)
data class GetAllUsersResponse(val response: StatusResponse, val users: List<UserWithId>)
data class LoginResponse(val response: StatusResponse, val message: String, val user: UserWithId?)
data class UpdateDifficultyResponse(val response: StatusResponse, val newDifficulty: Difficulty?, val message: String)
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
                    difficulty = Difficulty.values()[result.getInt("difficulty")]
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
            val data = user.user
            val statement = db!!.createStatement()
            val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
            val insertStatement = """
                INSERT INTO User (username, first_name, last_name, email, password, phone_number, last_logged_in, login_streak, difficulty)
                VALUES(
                '${data.username}', '${data.firstname}', '${data.lastname}', 
                '${data.email}', '${data.password}', '${data.phone_number}',
                '$currentDate', 1, ${Difficulty.Easy.ordinal}
                )
            """.trimIndent()
            val result = statement.executeUpdate(insertStatement, Statement.RETURN_GENERATED_KEYS)
            return if(result > 0) {
                val resultSet = statement.generatedKeys
                resultSet.next()

                val newId = resultSet.getInt(1)
                CreateUserResponse(StatusResponse.Success, "User created successfully", newId, data.username);
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
                    difficulty = Difficulty.values()[result.getInt("difficulty")]
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
                    difficulty = Difficulty.values()[result.getInt("difficulty")]
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
                    difficulty = Difficulty.values()[result.getInt("difficulty")]
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
}