package clarity.backend.controllers

import clarity.backend.DataManager
import clarity.backend.entity.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ClassroomController {

    // add class
    @PostMapping("/addClass")
    fun addClass(@RequestBody classroom: JoinClassroomEntity): ResponseEntity<String>  {
        val classroomEntity = ClassroomEntity()
        val joinResponse = classroomEntity.joinClass(classroom)
        return if(joinResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(joinResponse.id)
        } else {
            ResponseEntity.badRequest().body(joinResponse.id)
        }
    }

    // remove class

    // create class
    @PostMapping("/createClass")
    fun createClass(@RequestBody newClassroom: CreateClassroomEntity): ResponseEntity<String>  {
        val classroomEntity = ClassroomEntity()
        val classroomResponse = classroomEntity.createClass(newClassroom)

        return if(classroomResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(classroomResponse.id)
        } else {
            ResponseEntity.badRequest().body(classroomResponse.id)
        }
    }

    @GetMapping("/getClasses")
    fun getClasses(@RequestParam("id") id: String) : ResponseEntity<String> {
        val classroomEntity = ClassroomEntity()
        val classroomResponse = classroomEntity.getClasses(id.toInt())
        return if(classroomResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(classroomResponse.id.toString())
        } else {
            ResponseEntity.badRequest().body("Error occurred while querying")
        }
    }

    @GetMapping("/getClassesStudent")
    fun getClassesStudent(@RequestParam("id") id: String) : ResponseEntity<String> {
        val classroomEntity = ClassroomEntity()
        val classroomResponse = classroomEntity.getClassesStudent(id.toInt())
        return if(classroomResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(classroomResponse.id.toString())
        } else {
            ResponseEntity.badRequest().body("Error occurred while querying")
        }
    }
}