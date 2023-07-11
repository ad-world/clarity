package clarity.backend.controllers

import clarity.backend.entity.*
import clarity.backend.entity.CreateTaskResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TaskController {
    @PostMapping
    fun createTask(task: CreateTaskEntity) : ResponseEntity<CreateTaskResponse> {
        val taskEntity = TaskEntity()
        var response = taskEntity.createTask(task)
        return if(response.response == StatusResponse.Success) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.badRequest().body(response)
        }
    }

    @PostMapping
    fun getTasks(classId: GetTasksEntity) : ResponseEntity<GetTasksResponse> {
        val taskEntity = TaskEntity()
        var response = taskEntity.getAllTasksList(classId)
        return if(response.response == StatusResponse.Success) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.badRequest().body(response)
        }
    }
}