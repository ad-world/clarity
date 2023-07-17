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
class Following {

    @PostMapping("/follow")
    fun follow(@RequestBody request: FollowingRequestEntity) : ResponseEntity<FollowingResponse> {
        val followingEntity = FollowingEntity()
        val response = followingEntity.follow(request)
        return if(response.response == StatusResponse.Success) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.badRequest().body(response)
        }
    }
    @PostMapping("/unfollow")
    fun unfollow(@RequestBody request: FollowingRequestEntity) : ResponseEntity<FollowingResponse> {
        val followingEntity = FollowingEntity()
        val response = followingEntity.unfollow(request)
        return if(response.response == StatusResponse.Success) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.badRequest().body(response)
        }
    }

    @GetMapping("/getFollowing")
    fun getFollowing(@RequestParam userId: Int) : ResponseEntity<FollowerListResponse>{
        val followingEntity = FollowingEntity()
        val response = followingEntity.getFollowing(userId)
        return if(response.response == StatusResponse.Success) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.badRequest().body(response)
        }
    }

    @GetMapping("/getFollowers")
    fun getFollowers(@RequestParam userId: Int) : ResponseEntity<FollowerListResponse>{
        val followingEntity = FollowingEntity()
        val response = followingEntity.getFollowers(userId)
        return if(response.response == StatusResponse.Success) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.badRequest().body(response)
        }
    }

}