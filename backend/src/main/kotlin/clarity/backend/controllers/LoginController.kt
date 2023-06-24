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
class LoginController {
    val db = DataManager();

    @PostMapping("/login")
    fun login(@RequestBody user: UserLoginEntity): ResponseEntity<String> {
        val username = user.username
        val password = user.password
        val userEntity = UserEntity(db)

        if(username.isNotEmpty() && password.isNotEmpty() && userEntity.checkCredentials(user)) {
            return ResponseEntity.ok("Login successful")
        }
        return ResponseEntity.badRequest().body("Invalid credentials")
    }

    @PostMapping("/createUser")
    fun createUser(@RequestBody user: CreateUserEntity): ResponseEntity<String> {
        val userEntity = UserEntity(db);

        val newUserResponse = userEntity.createUser(user)

        return if(newUserResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(newUserResponse.message)
        } else {
            ResponseEntity.badRequest().body(newUserResponse.message)
        }

    }

    @GetMapping("/getUser")
    fun getUser(@RequestParam username: String): ResponseEntity<GetUserResponse> {
        val userEntity = UserEntity(db)
        val getUserResponse = userEntity.getUser(username)

        return if(username.isNotEmpty()) {
            if(getUserResponse.response == StatusResponse.Success) {
                ResponseEntity.ok(getUserResponse)
            } else {
                ResponseEntity.badRequest().body(getUserResponse)
            }
        } else {
            ResponseEntity.badRequest().body(
                GetUserResponse(
                    StatusResponse.Failure,
                    null,
                    "Please pass a username as a request param and try again."
                )
            )
        }
    }


}