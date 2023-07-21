package clarity.backend.controllers

import clarity.backend.entity.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {
    @PostMapping("/login")
    fun login(@RequestBody user: UserLoginEntity): ResponseEntity<LoginResponse> {
        val username = user.username
        val password = user.password
        val userEntity = UserEntity()

        val newUser = userEntity.checkCredentials(user)

        if(username.isNotEmpty() && password.isNotEmpty() && newUser != null ) {
            return ResponseEntity.ok(LoginResponse(StatusResponse.Success, "Login successful", newUser))
        }
        return ResponseEntity.badRequest().body(LoginResponse(StatusResponse.Failure, "Invalid credentials", null))
    }

    @PostMapping("/createUser")
    fun createUser(@RequestBody user: CreateUserEntity): ResponseEntity<CreateUserResponse> {
        val userEntity = UserEntity();

        val newUserResponse = userEntity.createUser(user)

        return if(newUserResponse.response == StatusResponse.Success) {
            ResponseEntity.ok(newUserResponse)
        } else {
            ResponseEntity.badRequest().body(newUserResponse)
        }

    }

    @GetMapping("/getUser")
    fun getUser(@RequestParam username: String): ResponseEntity<GetUserResponse> {
        val userEntity = UserEntity()
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

    @GetMapping("/getUserById")
    fun getUserById(@RequestParam userId: String): ResponseEntity<GetUserResponse> {
        val userEntity = UserEntity()
        val getUserResponse = userEntity.getUserById(userId)

        return if(userId.isNotEmpty()) {
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
    @GetMapping("/getAllUsers")
    fun getAllUsers(): ResponseEntity<GetAllUsersResponse> {
        val resp = UserEntity().getAllUsers()
        return if (resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }

    @PostMapping("/updateDifficulty")
    fun updateDifficulty(@RequestBody request: UpdateDifficultyEntity): ResponseEntity<UpdateDifficultyResponse> {
        val resp = UserEntity().updateDifficulty(request)

        return if(resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }
}