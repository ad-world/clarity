package clarity.backend.entity

import clarity.backend.DataManager
import java.util.Date

data class Task(val taskId: Int, val classId: String, val setId: Int, val name: String, val description: String, val dueDate: String?)
data class CreateTaskEntity(val classId: String, val sets: String, val name: String, val description: String, val dueDate: String)

data class GetTasksEntity(val classId: String)

data class CreateTaskResponse(val response: StatusResponse, val id: String)
data class GetTasksResponse(val response: StatusResponse, val id: List<Task>)
class TaskEntity() {
    fun createTask(task: CreateTaskEntity) : CreateTaskResponse{
        val db = DataManager.conn()
        try {
            val statement = db!!.createStatement()
            val insertStatement = """
                INSERT INTO Tasks (class_id, [set_id], name, description, due_date)
                VALUES(
                '${task.classId}', ${task.sets.toInt()}, '${task.name}', '${task.description}', '${task.dueDate}'
                )
            """.trimIndent()
            val result = statement.executeUpdate(insertStatement)
            return CreateTaskResponse(StatusResponse.Success, "Task Created")
        } catch(e: Exception) {
            e.printStackTrace();
            return CreateTaskResponse(StatusResponse.Failure, "Could not create the task")
        }
    }

    // returns a list of tasks associated with the classroom
    fun getAllTasksList(classroom: GetTasksEntity) : GetTasksResponse {
        try {
            val db = DataManager.conn()
            val statement = db!!.createStatement()
            val selectStatement = """
                SELECT * FROM Tasks WHERE class_id = '${classroom.classId}'
            """.trimIndent()
            val taskIds = mutableListOf<Task>()
            val result = statement.executeQuery(selectStatement)
            while (result.next()) {
                var newTask = Task(
                    result.getString("task_id").toInt(),
                    result.getString("class_id"),
                    result.getString("set_id").toInt(),
                    result.getString("name"),
                    result.getString("description"),
                    result.getString("due_date")
                    )
                taskIds.add(newTask)
            }
            return GetTasksResponse(StatusResponse.Success, taskIds)
        } catch(e: Exception) {
            e.printStackTrace();
            val resultPlaceholder = mutableListOf<Task>()
            return GetTasksResponse(StatusResponse.Failure, resultPlaceholder)
        }
    }
}