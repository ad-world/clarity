package clarity.backend.controllers

import clarity.backend.entity.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class InboxController {
    // getting all unread notifications
    @GetMapping("/getUnread")
    fun getUnread(@RequestParam userId: Int) : ResponseEntity<GetUnreadResponse>{
        val inbox = InboxEntity()
        val unreadMessages = inbox.getUnread(userId)
        return if(unreadMessages.response == StatusResponse.Success) {
            ResponseEntity.ok(unreadMessages)
        } else {
            ResponseEntity.badRequest().body(unreadMessages)
        }
    }

    // marking something as unread and read
    @PostMapping("/markMessage")
    fun markMessage(@RequestBody notification: MarkMessage) : ResponseEntity<NotificationResponse> {
        val inbox = InboxEntity()
        val unreadMessages = inbox.markMessage(notification)
        return if(unreadMessages.response == StatusResponse.Success) {
            ResponseEntity.ok(unreadMessages)
        } else {
            ResponseEntity.badRequest().body(unreadMessages)
        }
    }
    // delete notifications
    @DeleteMapping("/deleteMessage")
    fun deleteMessage(@RequestParam notificationId: Int) : ResponseEntity<NotificationResponse> {
        val inbox = InboxEntity()
        val unreadMessages = inbox.deleteMessage(notificationId)
        return if(unreadMessages.response == StatusResponse.Success) {
            ResponseEntity.ok(unreadMessages)
        } else {
            ResponseEntity.badRequest().body(unreadMessages)
        }
    }

    // create a notification and send an email notification as well
    @PostMapping("/createNotification")
    fun createNotification(@RequestBody notification: CreateNotification) : ResponseEntity<NotificationResponse> {
        val inbox = InboxEntity()
        val unreadMessages = inbox.createNotification(notification)
        return if(unreadMessages.response == StatusResponse.Success) {
            ResponseEntity.ok(unreadMessages)
        } else {
            ResponseEntity.badRequest().body(unreadMessages)
        }
    }
}