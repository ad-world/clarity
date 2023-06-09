package clarity.backend.controllers

import clarity.backend.entity.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class CardController {

    @PostMapping("/addCardToSet")
    fun addCardToSet(@RequestBody card: AddCardToSetRequest) : ResponseEntity<AddCardToSetResponse> {
        val resp = CardSetEntity().addCardToSet(card)
        return if (resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }

    @PostMapping("/deleteCardFromSet")
    fun deleteCardFromSet(@RequestBody card: DeleteCardFromSetRequest) : ResponseEntity<DeleteCardFromSetResponse> {
        val resp = CardSetEntity().deleteCardFromSet(card)
        return if (resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }

    @PostMapping("/addSet")
    fun addSet(@RequestBody cardSetEntity: CreateCardSetEntity) : ResponseEntity<CreateCardSetResponse> {
        val resp = CardSetEntity().createCardSet(cardSetEntity)
        return if (resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }

    @PostMapping("/getCardsForSet")
    fun getCards(@RequestBody set: GetCardsInSetRequest) : ResponseEntity<GetCardsInSetResponse> {
        val resp = CardSetEntity().getTotalCardsFromSet(set)
        return if (resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }

    @GetMapping("/getSets")
    fun getAllSets() : ResponseEntity<GetSetsResponse> {
        val resp = CardSetEntity().getSets()
        return if (resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }

    @PostMapping("/getDataForSet")
    fun getDataForSet(@RequestBody req: GetDataForSetRequest) : ResponseEntity<GetDataForSetResponse> {
        val resp = CardSetEntity().getDataForSet(req)
        return if (resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }

    @GetMapping("/getSetsByUsername")
    fun getSetByUsername(@RequestParam username: String): ResponseEntity<GetSetsByUsernameResponse> {
        val resp = CardSetEntity().getSetsByUsername(GetSetsByUsername(username));
        return if (resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }

    @PostMapping("/createCard")
    fun createCard(@RequestBody request: CreateCardEntity): ResponseEntity<CreateCardResponse> {
        val resp = CardEntity().createCard(request)
        return if (resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }

    @GetMapping("/searchPhrases")
    fun searchPhrases(@RequestParam phrase: PhraseSearchEntity): ResponseEntity<PhraseSearchResponse> {
        val resp = CardEntity().phraseSearch(phrase)
        return if(resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }

//    @PostMapping("/getProgressForSet")
//    fun getProgressForSet(@RequestBody request: GetProgressForSetRequest) : ResponseEntity<GetProgressForSetResponse> {
//        val resp = CardSetEntity().getProgressForSet(request)
//        return if (resp.response == StatusResponse.Success) {
//            ResponseEntity.ok(resp)
//        } else {
//            ResponseEntity.badRequest().body(resp)
//        }
//    }

//    @PostMapping("/updateProgressForSet")
//    fun updateProgressForSet(@RequestBody request: UpdateProgressForSetRequest) : ResponseEntity<UpdateProgressForSetResponse> {
//        val resp = CardSetEntity().updateProgressForSet(request)
//        return if (resp.response == StatusResponse.Success) {
//            ResponseEntity.ok(resp)
//        } else {
//            ResponseEntity.badRequest().body(resp)
//        }
//    }
}