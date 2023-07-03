package com.example.clarity.sdk

data class LoginRequest(val username: String, val password: String)
data class CreateUserEntity(val user: User)
data class User(val username: String, val email: String, val password: String, val firstname: String, val lastname: String, val phone_number: String)
data class JoinClassroomEntity(val privateCode: String, val userID: String)
data class CreateClassroomEntity(val name: String, val teacher: Integer)
data class CreateCardSetEntity(val creator_id: Int, val title: String, val type: String)
data class GetDataForSetRequest(val set_id: Int)
data class CreateCardEntity(val phrase: String, val title: String)
data class AddCardToSetRequest(val card_id: Int, val set_id: Int)
data class DeleteCardFromSetRequest(val card_id: Int, val set_id: Int)
data class GetCardsInSetRequest(val set_id: Int)
