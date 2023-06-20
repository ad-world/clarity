package clarity.backend.controllers

import clarity.backend.DataManager
import clarity.backend.entity.UserEntity
import clarity.backend.entity.UserLoginEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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


}