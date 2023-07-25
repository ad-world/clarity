package clarity.backend.controllers

import clarity.backend.entity.Announcement
import clarity.backend.entity.StatusResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

data class GetWordsResponse(val response: StatusResponse, val result: List<String>)
@RestController
class WordsController {
    @GetMapping("/getWords")
    fun getWords() : ResponseEntity<GetWordsResponse> {
        val jsonData = javaClass.classLoader.getResource("data.json")?.readText()
        // Deserialize JSON string to a list of Person objects
        val personList = Json.decodeFromString<List<String>>(jsonData!!)
        // Printing the list
        return ResponseEntity.ok(GetWordsResponse(StatusResponse.Success, personList))
    }
}