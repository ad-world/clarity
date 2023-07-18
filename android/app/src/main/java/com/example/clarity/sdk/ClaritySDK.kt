package com.example.clarity.sdk


import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
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

    @GET("getSetsByUsername")
    suspend fun getSetsByUsername(@Query("username") username: String): Response<GetSetsByUsernameResponse>

    @POST("attemptCard")
    suspend fun attemptCard(@Part("user_id") userId: Int, @Part("card_id") cardId: Int, @Part("set_id") setId: Int, @Part audio: MultipartBody.Part): Response<CreateAttemptResponse>

    @POST("getAttemptAverage")
    suspend fun getUserAverageAttempts(@Body request: GetUserAverageAttemptsRequest): Response<GetUserAverageAttemptsResponse>

    @POST("searchPhrases")
    suspend fun searchPhrases(@Query("phrase") phrase: PhraseSearchEntity): Response<PhraseSearchResponse>

    @POST("createCard")
    suspend fun createCard(@Body request: CreateCardEntity): Response<CreateCardResponse>

    @GET("getUserAttempts")
    suspend fun getUserAttempts(@Query("user") user: Int): Response<GetUserAttemptsResponse>

    @POST("getUserAttemptsForSet")
    suspend fun getUserAttemptsForSet(@Body request: GetAttemptsForSetEntity): Response<GetAttemptsForSetResponse>

    @POST("getSetProgress")
    suspend fun getSetProgress(@Body request: GetUserSetProgressRequest): Response<GetUserSetProgressResponse>

    @POST("classroom/attemptCard")
    suspend fun attemptClassroomCard(@Part("user_id") userId: Int, @Part("card_id") cardId: Int, @Part("task_id") task_id: Int, @Part audio: MultipartBody.Part): Response<CreateClassroomAttemptResponse>

    @GET("classroom/getTaskAttempts")
    suspend fun getTaskAttempts(@Query("task") task: Int): Response<GetTaskAttemptsResponse>

    @GET("classroom/getTaskProgress")
    suspend fun getTaskProgress(@Query("task_id") task_id: Int): Response<GetClassroomTaskProgressResponse>

    @GET("classroom/getClassAttempts")
    suspend fun getClassAttempts(@Query("classroom") classroom: String): Response<GetClassAttemptsResponse>



//    @POST("getProgressForSet")
//    suspend fun getProgressForSet(@Body req: GetProgressForSetRequest) : Response<GetProgressForSetResponse>
//
//    @POST("updateProgressForSet")
//    suspend fun updateProgressForSet(@Body req: UpdateProgressForSetRequest) : Response<UpdateProgressForSetResponse>
}