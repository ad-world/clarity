package clarity.backend.entity

import clarity.backend.DataManager

data class UserLoginEntity(val username: String, val password: String)

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
}