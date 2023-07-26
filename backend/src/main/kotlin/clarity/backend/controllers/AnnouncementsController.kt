package clarity.backend.controllers

import clarity.backend.entity.*
import clarity.backend.util.EmailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AnnouncementsController @Autowired constructor(val emailService: EmailService) {
    @PostMapping("/addAnnouncement")
    fun addAnouncement(@RequestBody post: CreateAnnouncementEntity) : ResponseEntity<AnnouncementResponse> {
        val announcementEntity = AnnouncementEntity()
        var response = announcementEntity.createAnnouncement(post)
        return if(response.response == StatusResponse.Success) {
            emailService.sendEmailNewAnnouncement(post)
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.badRequest().body(response)
        }
    }

    @DeleteMapping("/deleteAnnouncement")
    fun deleteAnnouncement(@RequestParam id: Int) : ResponseEntity<AnnouncementResponse> {
        val announcementEntity = AnnouncementEntity()
        val response = announcementEntity.deleteAnnouncement(id)
        return if(response.response == StatusResponse.Success) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.badRequest().body(response)
        }
    }

    @GetMapping("/getAnnouncements")
    fun getAnnouncements(@RequestParam classId: String) : ResponseEntity<GetAnnouncementsResponse> {
        val announcementEntity = AnnouncementEntity()
        val response = announcementEntity.getAnnouncements(classId)
        return if(response.response == StatusResponse.Success) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.badRequest().body(response)
        }
    }

}