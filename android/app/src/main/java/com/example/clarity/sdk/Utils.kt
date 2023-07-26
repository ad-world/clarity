package com.example.clarity.sdk

data class SetMetadata(val set_id: Int, val creator_id: Int, val title: String, val type: String, val is_public: Boolean, val likes: Int)
data class UserWithId(val user_id: Int, val username: String, val email: String, val firstname: String, val lastname: String, val phone_number: String, val login_streak: Int, val difficulty: Difficulty, val enableNotifications: Int)
data class ClassroomAttemptMetadata(
    val task_id: Int, val user_id: Int, val card_id: Int, val mispronunciations: List<String>,
    val omissions: List<String>, val insertions: List<String>, val pronunciationScore: Double, val accuracyScore: Double,
    val fluencyScore: Double, val completenessScore: Double, val speechAPIResponse: SpeechAPIResponse, val is_complete: Boolean)
data class AttemptMetadata(
    val set_id: Int, val user_id: Int, val card_id: Int, val mispronunciations: List<String>,
    val omissions: List<String>, val insertions: List<String>, val pronunciationScore: Double, val accuracyScore: Double,
    val fluencyScore: Double, val completenessScore: Double, val speechAPIResponse: SpeechAPIResponse, val is_complete: Boolean)
data class CardAttempt(val user_id: Int, val card_id: Int, val set_id: Int, val pronunciationScore: Double? = null, val accuracyScore: Double? = null,
                       val fluencyScore: Double? = null, val completenessScore: Double? = null, val attemptDate: String)
data class Card(val card_id: Int, val phrase: String, val title: String)
data class CardSet(val metadata: SetMetadata, val cards: List<Card>)
data class UserCreatedCardSet(val user_id: Int, val card_sets: List<CardSet>)
data class CardInSet(val card_id: Int, val set_id: Int, val completion_date: String?)

data class TaskAttemptWithName(val task_id: Int, val user_id: Int, val card_id: Int, val pronunciationScore: Int,
                               val accuracyScore: Int, val fluencyScore: Int, val completenessScore: Int, val attempt_date: String,
                               val firstName: String, val lastName: String)
data class TaskAttemptWithNameAndClass(val classroom: String, val task_id: Int, val user_id: Int, val card_id: Int, val pronunciationScore: Int,
                                       val accuracyScore: Int, val fluencyScore: Int, val completenessScore: Int, val attempt_date: String,
                                       val firstName: String, val lastName: String)
data class TaskWithProgress(val taskId: Int, val classId: String, val setId: Int, val name: String, val description: String, val dueDate: String?, val difficulty: Difficulty, val card_count: Int, val completed_card_count: Int)


data class StudentProgress(val user_id: Int, val completed_count: Int, val firstName: String, val lastName: String)

data class Task(val taskId: Int, val classId: String, val setId: Int, val name: String, val description: String, val dueDate: String?, val difficulty: Difficulty)

enum class Difficulty {
    Easy,
    Medium,
    Hard
}
data class PronunciationAssessment(
    val accuracyScore: Double?,
    val fluencyScore: Double?,
    val completenessScore: Double?,
    val pronScore: Double?,
    val errorType: String? // Add ErrorType field
)

data class Syllable(
    val syllable: String,
    val pronunciationAssessment: PronunciationAssessment,
    val offset: Int,
    val duration: Int
)

data class Phoneme(
    val phoneme: String,
    val pronunciationAssessment: PronunciationAssessment,
    val offset: Int,
    val duration: Int
)

data class Word(
    val word: String,
    val offset: Int,
    val duration: Int,
    val pronunciationAssessment: PronunciationAssessment,
    val syllables: List<Syllable>,
    val phonemes: List<Phoneme>,
    val errorType: String?
)

data class NBest(
    val confidence: Double,
    val lexical: String,
    val itn: String,
    val maskedItn: String,
    val display: String,
    val pronunciationAssessment: PronunciationAssessment,
    val words: List<Word>
)

data class SpeechAPIResponse(
    val id: String,
    val recognitionStatus: String? = null,
    val offset: Int? = null,
    val duration: Int? = null,
    val displayText: String? = null,
    val snr: Double? = null,
    val nBest: List<NBest>? = null
)