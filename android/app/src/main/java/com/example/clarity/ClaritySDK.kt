package com.example.clarity


import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

enum class StatusResponse {
    Success,
    Failure
}

data class LoginRequest(val username: String, val password: String)
data class User(val username: String, val email: String, val password: String, val firstname: String, val lastname: String, val phone_number: String)
data class CreateUserResponse(val response: StatusResponse, val message: String)
data class UserWithId(val user_id: Int, val username: String, val email: String, val firstname: String, val lastname: String, val phone_number: String)
data class GetUserResponse(val response: StatusResponse, val user: UserWithId?, val message: String)
data class JoinClassroomEntity(val privateCode: String, val userID: String)
data class CreateClassroomEntity(val name: String, val teacher: Integer)
data class CreateClassroomResponse(val response: StatusResponse, val id: String)
data class DeleteCard(val card_id: Int, val set_id: Int)
data class DeleteCardResponse(val response: StatusResponse, val msg: String)
data class CreateCardSetEntity(val creator_id: Int, val title: String, val type: String)
data class CreateCardSetResponse(val response: StatusResponse, val msg: String)
data class GetCardsInSet(val set_id: Int)
data class GetCardsInSetResponse(val response: StatusResponse, val cards: List<String>)
data class GetSetsResponse(val response: StatusResponse, val sets: List<String>)
data class GetDataForSetRequest(val set_id: Int)

data class GetDataForSetResponse(val response: StatusResponse, val data: List<String>)


data class LoginResponse(val success: Boolean, val message: String)

data class AddCard(val card_id: Int, val set_id: Int)

data class AddCardResponse(val response: StatusResponse, val msg: String)

data class JoinClassroomResponse(val response: StatusResponse, val id: String)

data class GetClassroomResponse(val response: StatusResponse, val id: List<String>)


class ClaritySDK {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: API = retrofit.create(API::class.java)
}

interface API {
    @POST("login")
    suspend fun login(@Body user: LoginRequest): Response<LoginResponse>

    @POST("createUser")
    suspend fun createUser(@Body user: User): Response<CreateUserResponse>

    @GET("getUser")
    suspend fun getUser(@Query("username") username: String): Response<GetUserResponse>

    @POST("addClass")
    suspend fun joinClass(@Body classroom: JoinClassroomEntity): Response<JoinClassroomResponse>

    @POST("createClass")
    suspend fun createClass(@Body classroom: CreateClassroomEntity): Response<CreateClassroomResponse>

    @GET("getClasses")
    suspend fun getClasses(@Query("id") id: String): Response<GetClassroomResponse>

    @GET("getClassesStudent")
    suspend fun getClassesStudent(@Query("id") id: String): Response<GetClassroomResponse>

    @POST("addCardToSet")
    suspend fun addCardToSet(@Body card: AddCard): Response<AddCardResponse>

    @POST("deleteCardFromSet")
    suspend fun deleteCardFromSet(@Body card: DeleteCard): Response<DeleteCardResponse>

    @POST("addSet")
    suspend fun addSet(@Body set: CreateCardSetEntity) : Response<CreateCardSetResponse>

    @POST("getCardsForSet")
    suspend fun getCards(@Body set: GetCardsInSet) : Response<GetCardsInSetResponse>

    @GET("getSets")
    suspend fun getAllSets() : Response<GetSetsResponse>

    @POST("getDataForSet")
    suspend fun  getDataForSet(@Body set: GetDataForSetRequest) : Response<GetDataForSetResponse>

}