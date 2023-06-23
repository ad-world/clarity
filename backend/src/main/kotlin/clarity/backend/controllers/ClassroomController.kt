package clarity.backend.controllers

import clarity.backend.DataManager
import clarity.backend.entity.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ClassroomController {
    val db = DataManager()

    // add class
    @PostMapping("/addClass")
    fun addClass(@RequestBody classroom: JoinClassroomEntity): ResponseEntity<String>  {
        val classroomEntity = ClassroomEntity(db)

        val response = classroomEntity.joinClass(classroom)

    }

    // remove class

    // create class
    @PostMapping("/createClass")
    fun createClass(@RequestBody newClassroom: ): ResponseEntity<String>  {
        val classroomEntity = ClassroomEntity(db)

        val response = classroomEntity.createClass(newClassroom)

    }
}