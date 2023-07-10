package clarity.backend.util

data class PronunciationAssessment(
    val AccuracyScore: Int?,
    val FluencyScore: Int?,
    val CompletenessScore: Int?,
    val PronScore: Int?,
    val ErrorType: String?

)

data class Syllable(
    val Syllable: String,
    val PronunciationAssessment: PronunciationAssessment,
    val Offset: Int,
    val Duration: Int
)

data class Phoneme(
    val Phoneme: String,
    val PronunciationAssessment: PronunciationAssessment,
    val Offset: Int,
    val Duration: Int
)

data class Word(
    val Word: String,
    val Offset: Int,
    val Duration: Int,
    val PronunciationAssessment: PronunciationAssessment,
    val Syllables: List<Syllable>,
    val Phonemes: List<Phoneme>,
)

data class NBest(
    val Confidence: Double,
    val Lexical: String,
    val ITN: String,
    val MaskedITN: String,
    val Display: String,
    val PronunciationAssessment: PronunciationAssessment,
    val Words: List<Word>
)

data class SpeechRecognitionResult(
    val Id: String,
    val RecognitionStatus: Int,
    val Offset: Int,
    val Duration: Int,
    val Channel: Int,
    val DisplayText: String,
    val SNR: Double,
    val NBest: List<NBest>
)