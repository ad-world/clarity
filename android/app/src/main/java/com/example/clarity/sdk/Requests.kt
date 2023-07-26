package com.example.clarity.sdk

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part


data class LoginRequest(val username: String, val password: String)
data class CreateUserEntity(val username: String, val email: String, val password: String, val firstname: String, val lastname: String, val phone_number: String, val difficulty: Difficulty)
data class JoinClassroomEntity(val privateCode: String, val userID: String)
data class CreateClassroomEntity(val name: String, val teacher: Int)
data class CreateCardSetEntity(val creator_id: Int, val title: String, val type: String)
data class CreateCardEntity(val phrase: String, val title: String, val setId: Int? = null)
data class AddCardToSetRequest(val card_id: Int, val set_id: Int)
data class DeleteCardFromSetRequest(val card_id: Int, val set_id: Int)
data class GetCardsInSetRequest(val set_id: Int)
data class GetUserSetProgressRequest(val set_id: Int, val user_id: Int)

data class GetUserSetProgressResponse(val response: StatusResponse, val set_id: Int, val user_id: Int, val numCards: Int, val numCompletedCards: Int, val cards: List<Card>, val completedCard: List<CardInSet>)

data class GetUserAverageAttemptsRequest(val user_id: Int)
data class PhraseSearchEntity(val phrase: String)
data class GetAttemptsForSetEntity(val user: Int, val set: Int)
data class CreateTaskEntity(val classId: String, val sets: String, val name: String, val description: String, val dueDate: String, val difficulty: Difficulty)
data class GetTasksEntity(val classId: String, val user_id: Int)
data class CreateAnnouncementEntity(val classId: String, val text: String, val description: String, val date: String)
data class FollowingRequestEntity(val userId: Int, val followingId: Int)
data class LikeCardSetRequest(val user_id: Int, val set_id: Int)
data class UnlikeCardSetRequest(val user_id: Int, val set_id: Int)
data class ToggleCardSetRequest(val set_id: Int)
data class UpdateDifficultyEntity(val userId: Int, val newDifficulty: Difficulty? = null)
data class UpdateTaskDifficultyEntity(val task_id: Int, val newDifficulty: Difficulty)
data class EditUserEntity(val user_id: Int, val firstname: String? = null, val lastname: String? = null, val email: String? = null, val enableNotifications: Int? = null)
data class ChangePasswordEntity(val user_id: Int, val old_password: String, val new_password: String)
data class GetSetDataRequest(val set_id: Int)
data class ClonePublicSetRequest(val set_id: Int, val user_id: Int)
data class MarkMessage(val notificationId: Int, val isRead: Int)
data class GetCardSetsForFollowingRequest(val user_id: Int)


