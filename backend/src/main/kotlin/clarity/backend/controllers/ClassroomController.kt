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
    val db = DataManager()

    // add class
//    @PostMapping("/addClass")
//    fun addClass(@RequestBody classroom: JoinClassroomEntity): ResponseEntity<String>  {
//        val classroomEntity = ClassroomEntity(db)
//
//        val response = classroomEntity.joinClass(classroom)
//
//    }

    // remove class

    // create class
    @PostMapping("/createClass")
    fun createClass(@RequestBody newClassroom: CreateClassroomEntity): ResponseEntity<String>  {
        val classroomEntity = ClassroomEntity(db)
        val classroomResponse = classroomEntity.createClass(newClassroom)

        return if(classroomResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(classroomResponse.id)
        } else {
            ResponseEntity.badRequest().body(classroomResponse.id)
        }
    }

    @GetMapping("/getClasses")
    fun getClasses(@RequestParam("id") id: String) : ResponseEntity<String> {
        val classroomEntity = ClassroomEntity(db)
        val classroomResponse = classroomEntity.getClasses(id.toInt())
        return if(classroomResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(classroomResponse.id.toString())
        } else {
            ResponseEntity.badRequest().body("Error occurred while querying")
        }
    }
}