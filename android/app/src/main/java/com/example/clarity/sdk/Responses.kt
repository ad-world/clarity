package com.example.clarity.sdk

data class CreateUserResponse(val response: StatusResponse, val message: String)
data class GetUserResponse(val response: StatusResponse, val user: UserWithId?, val message: String)
data class CreateClassroomResponse(val response: StatusResponse, val id: String)
data class DeleteCardFromSetResponse(val response: StatusResponse, val msg: String)
data class CreateCardSetResponse(val response: StatusResponse, val msg: String)
data class GetCardsInSetResponse(val response: StatusResponse, val cards: List<String>)
data class GetSetsResponse(val response: StatusResponse, val sets: List<String>)
data class CreateCardResponse(val response: StatusResponse, val msg: String)
data class EvaluateResponse(val response: StatusResponse, val score: Int)
data class AddCardToSetResponse(val response: StatusResponse, val msg: String)
data class GetSetsByUsernameResponse(val response: StatusResponse, val data: List<SetMetadata>)
