package clarity.backend.entity

import clarity.backend.DataManager

enum class StatusResponse {
    Success,
    Failure
}

data class User(val username: String, val email: String, val password: String, val firstname: String, val lastname: String, val phone_number: String)
data class UserWithId(val user_id: Int, val username: String, val email: String, val firstname: String, val lastname: String, val phone_number: String)
data class UserLoginEntity(val username: String, val password: String)
data class CreateUserEntity(val user: User)
data class CreateUserResponse(val response: StatusResponse, val message: String)
data class GetUserResponse(val response: StatusResponse, val user: UserWithId?, val message: String)

data class LoginResponse(val response: StatusResponse, val message: String, val user: UserWithId?)

class UserEntity() {

    fun checkCredentials(user: UserLoginEntity): UserWithId? {
        val db = DataManager.conn()

        try {
            val statement = db!!.createStatement()
            val selectStatement = "SELECT * FROM User WHERE username = '${user.username}' AND password = '${user.password}'";
            val result = statement.executeQuery(selectStatement);
            if(result.next()) {
                return UserWithId(
                    username = user.username,
                    email = result.getString("email"),
                    firstname = result.getString("first_name"),
                    lastname = result.getString("last_name"),
                    user_id = result.getInt("user_id"),
                    phone_number = result.getString("phone_number")
                )
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
            val insertStatement = """
                INSERT INTO User (username, first_name, last_name, email, password, phone_number)
                VALUES(
                '${data.username}', '${data.firstname}', '${data.lastname}', 
                '${data.email}', '${data.password}', '${data.phone_number}'
                )
            """.trimIndent()
            val result = statement.executeUpdate(insertStatement)
            return CreateUserResponse(StatusResponse.Success, "User created successfully")
        } catch(e: Exception) {
            e.printStackTrace();
            return CreateUserResponse(StatusResponse.Failure, e.message ?: "Unknown error")
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
                    phone_number = result.getString("phone_number")
                )
                GetUserResponse(StatusResponse.Success, user, "User found successfully")
            } else {
                GetUserResponse(StatusResponse.Failure, null, "Unable to find user with username $username");
            }
        } catch (e: Exception) {
            return GetUserResponse(StatusResponse.Failure, null, "Unknown error: ${e.message}")
        }
    }
}