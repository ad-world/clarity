package clarity.backend.entity

import clarity.backend.DataManager
import clarity.backend.util.Difficulty

data class Task(val taskId: Int, val classId: String, val setId: Int, val name: String, val description: String, val dueDate: String?, val difficulty: Difficulty)
data class CreateTaskEntity(val classId: String, val sets: String, val name: String, val description: String, val dueDate: String, val difficulty: Difficulty)

data class GetTasksEntity(val classId: String)

data class UpdateTaskDifficultyEntity(val task_id: Int, val newDifficulty: Difficulty)

data class CreateTaskResponse(val response: StatusResponse, val id: String)
data class GetTasksResponse(val response: StatusResponse, val id: List<Task>)
data class GetTaskResponse(val response: StatusResponse, val task: Task?)
data class UpdateTaskDifficultyResponse(val response: StatusResponse, val newDifficulty: Difficulty? = null, val message: String)

class TaskEntity() {
    fun createTask(task: CreateTaskEntity) : CreateTaskResponse{
        val db = DataManager.conn()
        return try {
            val statement = db!!.createStatement()
            val insertStatement = """
                    INSERT INTO Tasks (class_id, [set_id], name, description, due_date, difficulty)
                    VALUES(
                   '${task.classId}', ${task.sets.toInt()}, '${task.name}', '${task.description}', '${task.dueDate}', ${task.difficulty.ordinal}
                    )
                """.trimIndent()
            val result = statement.executeUpdate(insertStatement)
            CreateTaskResponse(StatusResponse.Success, "Task Created")
        } catch(e: Exception) {
            e.printStackTrace();
            CreateTaskResponse(StatusResponse.Failure, "Could not create the task")
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
                val newTask = Task(
                    result.getString("task_id").toInt(),
                    result.getString("class_id"),
                    result.getString("set_id").toInt(),
                    result.getString("name"),
                    result.getString("description"),
                    result.getString("due_date"),
                    Difficulty.values()[result.getInt("difficulty")]
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

    // get a single task by ID
    fun getTaskById(taskId: Int) : GetTaskResponse {
        val db = DataManager.conn()

        try {
            val statement = db!!.createStatement()
            val query = "SELECT * FROM Tasks WHERE task_id = $taskId"
            val result = statement.executeQuery(query)

            if(result.next()) {
                return GetTaskResponse(
                    StatusResponse.Success,
                    Task(
                        taskId = taskId,
                        classId = result.getString("class_id"),
                        setId = result.getInt("set_id"),
                        description = result.getString("description"),
                        name = result.getString("name"),
                        dueDate = result.getString("due_date"),
                        difficulty = Difficulty.values()[result.getInt("difficulty")]
                    )
                )
            } else {
                return GetTaskResponse(
                    StatusResponse.Failure,
                    null
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return GetTaskResponse(StatusResponse.Failure, null)
        }
    }

    fun updateTaskDifficulty(request: UpdateTaskDifficultyEntity): UpdateTaskDifficultyResponse {
        val db = DataManager.conn()

        try {
            val statement = db!!.createStatement()
            val updateStatement = """
                UPDATE Tasks SET difficulty = ${request.newDifficulty.ordinal} WHERE task_id = ${request.task_id}
            """.trimIndent()
            val result = statement.executeUpdate(updateStatement)

            return if(result > 0) {
                UpdateTaskDifficultyResponse(
                    StatusResponse.Success,
                    request.newDifficulty,
                    "Difficulty updated successfully"
                )
            } else {
                UpdateTaskDifficultyResponse(
                    StatusResponse.Failure,
                    request.newDifficulty,
                    "Could not update difficulty"
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return UpdateTaskDifficultyResponse(
                StatusResponse.Failure,
                null,
                e.message ?: "Unknown error"
            )
        }
    }
}