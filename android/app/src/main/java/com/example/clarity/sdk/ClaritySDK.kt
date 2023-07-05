package com.example.clarity.sdk


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
    suspend fun createUser(@Body user: CreateUserEntity): Response<CreateUserResponse>

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
    suspend fun addCardToSet(@Body card: AddCardToSetRequest): Response<AddCardToSetResponse>

    @POST("deleteCardFromSet")
    suspend fun deleteCardFromSet(@Body card: DeleteCardFromSetRequest): Response<DeleteCardFromSetResponse>

    @POST("addSet")
    suspend fun addSet(@Body set: CreateCardSetEntity) : Response<CreateCardSetResponse>

    @POST("getCardsForSet")
    suspend fun getCards(@Body set: GetCardsInSetRequest) : Response<GetCardsInSetResponse>

    @GET("getSets")
    suspend fun getAllSets() : Response<GetSetsResponse>

    @POST("getDataForSet")
    suspend fun  getDataForSet(@Body set: GetDataForSetRequest) : Response<GetDataForSetResponse>

    @GET
    suspend fun getSetsByUsername(@Query("username") username: String): Response<GetSetsByUsernameResponse>

    @POST("getProgressForSet")
    suspend fun getProgressForSet(@Body req: GetProgressForSetRequest) : Response<GetProgressForSetResponse>

    @POST("updateProgressForSet")
    suspend fun updateProgressForSet(@Body req: UpdateProgressForSetRequest) : Response<UpdateProgressForSetResponse>
}