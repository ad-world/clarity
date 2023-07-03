package com.example.clarity.sdk

data class Evaluate(val user_recording: String) // Just wrote it as string for now.
data class SetMetadata(val set_id: Int, val title: String, val type: String)
data class UserWithId(val user_id: Int, val username: String, val email: String, val firstname: String, val lastname: String, val phone_number: String)
