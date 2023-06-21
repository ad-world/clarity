package clarity.backend.entity

import clarity.backend.DataManager

enum class StatusResponse {
    Success,
    Failure
}

data class UserLoginEntity(val username: String, val password: String)
data class CreateUserEntity(val username: String, val email: String, val password: String, val firstname: String, val lastname: String, val phone_number: String)
data class CreateUserResponse(val response: StatusResponse, val message: String)

class UserEntity(dataManager: DataManager) {
    private val db = dataManager.db

    fun checkCredentials(user: UserLoginEntity): Boolean {
        try {
            val statement = db.createStatement()
            val selectStatement = "SELECT * FROM User WHERE username = '${user.username}' AND password = '${user.password}'";
            val result = statement.executeQuery(selectStatement);
            if(result.next()) {
                return true;
            }

            return false;
        } catch (e: Exception) {
            e.printStackTrace();
            return false;
        }
    }

    fun createUser(user: CreateUserEntity): CreateUserResponse {
        try {
            val statement = db.createStatement()
            val insertStatement = """
                INSERT INTO User (username, first_name, last_name, email, password, phone_number)
                VALUES(
                '${user.username}', '${user.firstname}', '${user.lastname}', 
                '${user.email}', '${user.password}', '${user.phone_number}'
                )
            """.trimIndent()
            val result = statement.executeUpdate(insertStatement)

            return CreateUserResponse(StatusResponse.Success, "User created successfully")
        } catch(e: Exception) {
            e.printStackTrace();
            return CreateUserResponse(StatusResponse.Failure, e.message ?: "Unknown error")
        }
    }
}