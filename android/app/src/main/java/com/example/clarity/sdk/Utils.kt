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

data class Card(val card_id: Int, val phrase: String, val title: String)
