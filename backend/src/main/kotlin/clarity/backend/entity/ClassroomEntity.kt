package clarity.backend.entity

import clarity.backend.DataManager
import java.util.UUID

// request formats
data class JoinClassroomEntity(val privateCode: String, val username: String, val firstname: String, val lastname: String)
data class CreateClassroomEntity(val name: String, val teacher: Integer)


// response formats
data class CreateClassroomResponse(val response: StatusResponse, val id: String)

data class GetClassroomResponse(val response: StatusResponse, val id: List<String>)

class ClassroomEntity(dataManager: DataManager) {
    private val db = dataManager.db

//    fun joinClass(classroom : JoinClassroomEntity) {
//        try {
//            val statement = db.createStatement()
//
//        }
//    }

    fun createClass(classroom: CreateClassroomEntity) : CreateClassroomResponse {
        try {
            val statement = db.createStatement()
            val newUUID = UUID.randomUUID().toString()
            val insertStatement = """
                INSERT INTO Classroom (private_code, name, teacher)
                VALUES(
                '${newUUID}', '${classroom.name}', '${classroom.teacher}'
                )
            """.trimIndent()
            val result = statement.executeUpdate(insertStatement)

            return CreateClassroomResponse(StatusResponse.Success, newUUID)
        } catch(e: Exception) {
            e.printStackTrace();
            return CreateClassroomResponse(StatusResponse.Failure, "Failed to create class")
        }
    }

    fun getClasses(userId: Int) : GetClassroomResponse{
        try {
            val statement = db.createStatement()
            val selectStatement = """
                SELECT * FROM Classroom WHERE teacher = $userId
            """.trimIndent()
            val classNames = mutableListOf<String>()
            val result = statement.executeQuery(selectStatement)
            while (result.next()) {
                var className = result.getString("name")
                classNames.add(className)
            }

            return GetClassroomResponse(StatusResponse.Success, classNames)
        } catch(e: Exception) {
            e.printStackTrace();
            val resultPlaceholder = mutableListOf<String>()
            return GetClassroomResponse(StatusResponse.Failure, resultPlaceholder)
        }
    }
}