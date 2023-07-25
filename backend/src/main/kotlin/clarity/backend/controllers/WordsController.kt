package clarity.backend.controllers

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WordsController {
    @GetMapping("/getWords")
    fun getWords() : ResponseEntity<List<String>> {
        val jsonData = javaClass.classLoader.getResource("data.json")?.readText()
        // Deserialize JSON string to a list of Person objects
        val personList = Json.decodeFromString<List<String>>(jsonData!!)
        // Printing the list
        return ResponseEntity.ok(personList)
    }
}