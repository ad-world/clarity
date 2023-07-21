package clarity.backend.controllers

import clarity.backend.entity.*
import clarity.backend.entity.CreateTaskResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TaskController {

    @PostMapping("/createTask")
    fun createTask(@RequestBody task: CreateTaskEntity) : ResponseEntity<CreateTaskResponse> {
        val taskEntity = TaskEntity()
        val response = taskEntity.createTask(task)
        return if(response.response == StatusResponse.Success) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.badRequest().body(response)
        }
    }

    @PostMapping("/getTasksList")
    fun getTasks(@RequestBody classId: GetTasksEntity) : ResponseEntity<GetTasksResponse> {
        val taskEntity = TaskEntity()
        val response = taskEntity.getAllTasksList(classId)
        return if(response.response == StatusResponse.Success) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.badRequest().body(response)
        }
    }

    @PostMapping("/updateTaskDifficulty")
    fun updateTaskDifficulty(@RequestBody request: UpdateTaskDifficultyEntity): ResponseEntity<UpdateTaskDifficultyResponse> {
        val resp = TaskEntity().updateTaskDifficulty(request);

        return if(resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }
}