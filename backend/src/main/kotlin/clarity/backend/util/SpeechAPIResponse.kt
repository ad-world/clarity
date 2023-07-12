import com.google.gson.annotations.SerializedName

data class PronunciationAssessment(
    @SerializedName("AccuracyScore")
    val accuracyScore: Double?,
    @SerializedName("FluencyScore")
    val fluencyScore: Double?,
    @SerializedName("CompletenessScore")
    val completenessScore: Double?,
    @SerializedName("PronScore")
    val pronScore: Double?,
    @SerializedName("ErrorType")
    val errorType: String? // Add ErrorType field
)

data class Syllable(
    @SerializedName("Syllable")
    val syllable: String,
    @SerializedName("PronunciationAssessment")
    val pronunciationAssessment: PronunciationAssessment,
    @SerializedName("Offset")
    val offset: Int,
    @SerializedName("Duration")
    val duration: Int
)

data class Phoneme(
    @SerializedName("Phoneme")
    val phoneme: String,
    @SerializedName("PronunciationAssessment")
    val pronunciationAssessment: PronunciationAssessment,
    @SerializedName("Offset")
    val offset: Int,
    @SerializedName("Duration")
    val duration: Int
)

data class Word(
    @SerializedName("Word")
    val word: String,
    @SerializedName("Offset")
    val offset: Int,
    @SerializedName("Duration")
    val duration: Int,
    @SerializedName("PronunciationAssessment")
    val pronunciationAssessment: PronunciationAssessment,
    @SerializedName("Syllables")
    val syllables: List<Syllable>,
    @SerializedName("Phonemes")
    val phonemes: List<Phoneme>,
    @SerializedName("ErrorType") // Add ErrorType field
    val errorType: String?
)

data class NBest(
    @SerializedName("Confidence")
    val confidence: Double,
    @SerializedName("Lexical")
    val lexical: String,
    @SerializedName("ITN")
    val itn: String,
    @SerializedName("MaskedITN")
    val maskedItn: String,
    @SerializedName("Display")
    val display: String,
    @SerializedName("PronunciationAssessment")
    val pronunciationAssessment: PronunciationAssessment,
    @SerializedName("Words")
    val words: List<Word>
)

data class SpeechAPIResponse(
    @SerializedName("Id")
    val id: String,
    @SerializedName("RecognitionStatus")
    val recognitionStatus: String? = null,
    @SerializedName("Offset")
    val offset: Int? = null,
    @SerializedName("Duration")
    val duration: Int? = null,
    @SerializedName("DisplayText")
    val displayText: String? = null,
    @SerializedName("SNR")
    val snr: Double? = null,
    @SerializedName("NBest")
    val nBest: List<NBest>? = null
)