package clarity.backend.entity

import clarity.backend.DataManager

// request formats
data class JoinClassroomEntity(val privateCode: String, val userID: String)
data class CreateClassroomEntity(val name: String, val teacher: Integer)


// response formats
data class CreateClassroomResponse(val response: StatusResponse, val id: String)

data class JoinClassroomResponse(val response: StatusResponse, val id: String)

data class GetClassroomResponse(val response: StatusResponse, val id: List<ClassroomReturnObject>)

data class ClassroomReturnObject(val code: String, val name: String)

class ClassroomEntity() {
    private fun getRandomString(length: Int) : String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }
    fun joinClass(classroom : JoinClassroomEntity) : JoinClassroomResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val insertStatement = """
                INSERT INTO ClassroomStudents (class_id, user_id)
                VALUES(
                '${classroom.privateCode}', '${classroom.userID.toInt()}'
                )
            """.trimIndent()
            val result = statement.executeUpdate(insertStatement)
            return JoinClassroomResponse(StatusResponse.Success, "Classroom Joined")
        } catch(e: Exception) {
            e.printStackTrace();
            return JoinClassroomResponse(StatusResponse.Failure, "Failed to join class")
        }
    }

    fun createClass(classroom: CreateClassroomEntity) : CreateClassroomResponse {
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            // must check if new string already exists in the table
            var newId = getRandomString(6)
            val selectStatement = """
                SELECT private_code FROM Classroom
            """.trimIndent()
            val ids = mutableListOf<String>()
            val resultCheck = statement.executeQuery(selectStatement)
            if (resultCheck.next()) {
                var className = resultCheck.getString("private_code")
                ids.add(className)
            }
            while (newId in ids) {
                newId = getRandomString(6)
            }
            val insertStatement = """
                INSERT INTO Classroom (private_code, name, teacher)
                VALUES(
                '${newId}', '${classroom.name}', '${classroom.teacher}'
                )
            """.trimIndent()
            val result = statement.executeUpdate(insertStatement)
            return CreateClassroomResponse(StatusResponse.Success, newId)
        } catch(e: Exception) {
            e.printStackTrace();
            return CreateClassroomResponse(StatusResponse.Failure, "Failed to create class")
        }
    }

    fun getClasses(userId: Int) : GetClassroomResponse{
        val db = DataManager.conn()

        try {
            val statement = db!!.createStatement()
            val selectStatement = """
                SELECT * FROM Classroom WHERE teacher = $userId
            """.trimIndent()
            val classNames = mutableListOf<ClassroomReturnObject>()
            val result = statement.executeQuery(selectStatement)
            while (result.next()) {
                var className = ClassroomReturnObject(
                    result.getString("private_code"),
                    result.getString("name")
                )
                classNames.add(className)
            }
            return GetClassroomResponse(StatusResponse.Success, classNames)
        } catch(e: Exception) {
            e.printStackTrace();
            val resultPlaceholder = mutableListOf<ClassroomReturnObject>()
            return GetClassroomResponse(StatusResponse.Failure, resultPlaceholder)
        }
    }

    fun getClassesStudent(userId: Int) : GetClassroomResponse{
        val db = DataManager.conn()

        try {
            val statement = db!!.createStatement()
            val selectStatement = """
                SELECT * FROM ClassroomStudents WHERE user_id = $userId
            """.trimIndent()
            val classNames = mutableListOf<ClassroomReturnObject>()
            val result = statement.executeQuery(selectStatement)
            while (result.next()) {
                var className = ClassroomReturnObject(
                    result.getString("private_code"),
                    result.getString("name")
                )
                classNames.add(className)
            }
            return GetClassroomResponse(StatusResponse.Success, classNames)
        } catch(e: Exception) {
            e.printStackTrace();
            val resultPlaceholder = mutableListOf<ClassroomReturnObject>()
            return GetClassroomResponse(StatusResponse.Failure, resultPlaceholder)
        }
    }
}