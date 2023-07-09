package com.example.clarity.sdk

data class Evaluate(val user_recording: String) // Just wrote it as string for now.
data class SetMetadata(val set_id: Int, val title: String, val type: String)
data class UserWithId(val user_id: Int, val username: String, val email: String, val firstname: String, val lastname: String, val phone_number: String, val login_streak: Int)
data class ClassroomAttemptMetadata(
    val task_id: Int, val user_id: Int, val card_id: Int, val mispronunciations: List<String>,
    val omissions: List<String>, val insertions: List<String>, val pronunciationScore: Int, val accuracyScore: Int,
    val fluencyScore: Int, val completenessScore: Int)
data class AttemptMetadata(
    val set_id: Int, val user_id: Int, val card_id: Int, val mispronunciations: List<String>,
    val omissions: List<String>, val insertions: List<String>, val pronunciationScore: Int, val accuracyScore: Int,
    val fluencyScore: Int, val completenessScore: Int)

data class CardAttempt(val user_id: Int, val card_id: Int, val set_id: Int, val pronunciationScore: Double? = null, val accuracyScore: Double? = null,
                       val fluencyScore: Double? = null, val completenessScore: Double? = null, val attemptDate: String)
data class Card(val card_id: Int, val phrase: String, val title: String)

data class TaskAttemptWithName(val task_id: Int, val user_id: Int, val card_id: Int, val pronunciationScore: Int,
                               val accuracyScore: Int, val fluencyScore: Int, val completenessScore: Int, val attempt_date: String,
                               val firstName: String, val lastName: String)
data class TaskAttemptWithNameAndClass(val classroom: String, val task_id: Int, val user_id: Int, val card_id: Int, val pronunciationScore: Int,
                                       val accuracyScore: Int, val fluencyScore: Int, val completenessScore: Int, val attempt_date: String,
                                       val firstName: String, val lastName: String)