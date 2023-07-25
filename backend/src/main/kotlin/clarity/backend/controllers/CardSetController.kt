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

    @GetMapping("/getSetIDs")
    fun getSetIDs() : ResponseEntity<GetSetIDsResponse> {
        val resp = CardSetEntity().getSetIDs()
        return if (resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }

    @PostMapping("/getSetData")
    fun getSetData(@RequestBody request: GetSetDataRequest): ResponseEntity<GetSetDataResponse> {
        val resp = CardSetEntity().getSetData(request)
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

    @PostMapping("/getSetProgress")
    fun getSetProgress(@RequestBody request: GetUserSetProgressRequest): ResponseEntity<GetUserSetProgressResponse> {
        val resp = CardSetEntity().getUserSetProgress(request)
        return if(resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }

    // this endpoint won't be used, just have it here so that I don't need to implement completing algorithm just yet
    @PostMapping("/completeCard")
    fun completeCard(@RequestBody request: CompleteCardRequest): ResponseEntity<CompleteCardResponse> {
        val resp = CardSetEntity().completeCardInUserSet(request)
        return if(resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }

    @GetMapping("/getPublicCardSetsOrderedByLikes")
    fun getPublicCardSetsOrderedByLikes(): ResponseEntity<getPublicCardSetsOrderedByLikesResponse> {
        val resp = CardSetEntity().getPublicCardSetsOrderedByLikes()
        return if (resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }

    @PostMapping("/likeCardSet")
    fun likeCardSet(@RequestBody request: LikeCardSetRequest): ResponseEntity<LikeCardSetResponse> {
        val resp = CardSetEntity().likeCardSet(request)
        return if (resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }

    @PostMapping("/unlikeCardSet")
    fun unlikeCardSet(@RequestBody request: UnlikeCardSetRequest): ResponseEntity<UnlikeCardSetResponse> {
        val resp = CardSetEntity().unlikeCardSet(request)
        return if (resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }

    @PostMapping("/toggleCardSetVisibility")
    fun toggleCardSetVisibility(@RequestBody request: ToggleCardSetRequest): ResponseEntity<ToggleCardSetResponse> {
        val resp = CardSetEntity().toggleCardSetVisibility(request)
        return if (resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }

    @GetMapping("/getPublicCardSets")
    fun getPublicCardSets(): ResponseEntity<GetPublicCardSetsResponse> {
        val resp = CardSetEntity().getPublicCardSets()
        return if (resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }

    @PostMapping("/clonePublicSet")
    fun clonePublicSet(@RequestBody request: ClonePublicSetRequest): ResponseEntity<ClonePublicSetResponse> {
        val resp = CardSetEntity().clonePublicSet(request)
        return if (resp.response == StatusResponse.Success) {
            ResponseEntity.ok(resp)
        } else {
            ResponseEntity.badRequest().body(resp)
        }
    }

}