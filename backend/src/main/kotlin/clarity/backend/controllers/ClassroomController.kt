package clarity.backend.controllers

import clarity.backend.DataManager
import clarity.backend.entity.*
import clarity.backend.util.EmailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ClassroomController{
    // add class
    @PostMapping("/addClass")
    fun addClass(@RequestBody classroom: JoinClassroomEntity): ResponseEntity<JoinClassroomResponse>  {
        val classroomEntity = ClassroomEntity()
        val joinResponse = classroomEntity.joinClass(classroom)
        return if(joinResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(joinResponse)
        } else {
            ResponseEntity.badRequest().body(joinResponse)
        }
    }

    // remove class

    // create class
    @PostMapping("/createClass")
    fun createClass(@RequestBody newClassroom: CreateClassroomEntity): ResponseEntity<CreateClassroomResponse>  {
        val classroomEntity = ClassroomEntity()
        val classroomResponse = classroomEntity.createClass(newClassroom)

        return if(classroomResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(classroomResponse)
        } else {
            ResponseEntity.badRequest().body(classroomResponse)
        }
    }

    @GetMapping("/getClasses")
    fun getClasses(@RequestParam("id") id: String) : ResponseEntity<GetClassroomResponse> {
        val classroomEntity = ClassroomEntity()
        val classroomResponse = classroomEntity.getClasses(id.toInt())
        return if(classroomResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(classroomResponse)
        } else {
            ResponseEntity.badRequest().body(classroomResponse)
        }
    }

    @GetMapping("/getClassesStudent")
    fun getClassesStudent(@RequestParam("id") id: String) : ResponseEntity<GetClassroomResponse> {
        val classroomEntity = ClassroomEntity()
        val classroomResponse = classroomEntity.getClassesStudent(id.toInt())
        return if(classroomResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(classroomResponse)
        } else {
            ResponseEntity.badRequest().body(classroomResponse)
        }
    }
}