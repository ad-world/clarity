package clarity.backend.controllers

import clarity.backend.entity.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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
        println(resp)
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
}