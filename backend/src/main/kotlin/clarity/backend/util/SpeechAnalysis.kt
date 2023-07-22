package clarity.backend.util

import SpeechAPIResponse
import TemporaryFileStorage
import clarity.backend.util.Difficulty
import com.google.gson.Gson
import com.microsoft.cognitiveservices.speech.*
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit


data class SpeechAnalysisResult(val json: SpeechAPIResponse, val assessmentResult: PronunciationAssessmentResult?)

class SpeechAnalysis {
    private var dotenv: Dotenv = dotenv {
        ignoreIfMissing = true
    }
    private var speechRegion: String
    private var speechKey: String
    private var speechConfig: SpeechConfig?
    init {
        try {
            dotenv = dotenv {
                ignoreIfMissing = true
            }
            speechRegion = dotenv["SPEECH_REGION"]?: ""
            speechKey = dotenv["SPEECH_KEY"]?: ""
            speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion)
        } catch (e: Exception) {
            speechKey = ""
            speechRegion = ""
            speechConfig = null
            println(e.message)
            e.printStackTrace()
        }
    }


    fun analyzeAudio(file: MultipartFile, phrase: String): SpeechAnalysisResult? {
        try {
            val tempFile = TemporaryFileStorage();
            val filePath = tempFile.storeFile(file)
            val config: AudioConfig = AudioConfig.fromWavFileInput(filePath)

            println("HIHIHIHIHIHIHIHIHIH Filepath is: $filePath")
            println("isWavFile ${isWavFile(File(filePath))}")

            val speechRecognizer = SpeechRecognizer(speechConfig, config)
            val assessmentConfig = PronunciationAssessmentConfig(
                phrase,
                PronunciationAssessmentGradingSystem.HundredMark,
                PronunciationAssessmentGranularity.Phoneme,
                true
            )
            println("hello1")

            assessmentConfig.applyTo(speechRecognizer)

            val future = speechRecognizer.recognizeOnceAsync();
            val result = future.get(10, TimeUnit.SECONDS);

            val pronunciationAssessmentResult = PronunciationAssessmentResult.fromResult(result)
            val pronunciationAssessmentResultJson = result.properties.getProperty(PropertyId.SpeechServiceResponse_JsonResult);

            val gson = Gson()
            val parsedResponse: SpeechAPIResponse = gson.fromJson(pronunciationAssessmentResultJson, SpeechAPIResponse::class.java)

            println("hello2")

            speechRecognizer.close()
            config.close()
            assessmentConfig.close()
            result.close()

            println("hello3")

            tempFile.deleteFile(filePath)

            println("hello4")

            return SpeechAnalysisResult(json = parsedResponse, assessmentResult = pronunciationAssessmentResult)
        } catch (e: Exception) {
            e.printStackTrace();
            return null;
        }
    }

    fun isWavFile(wavFile: File): Boolean {
        if (!wavFile.exists()) {
            return false
        }

        val headerSize = 12 // The WAV header size is 12 bytes
        val buffer = ByteArray(headerSize)

        try {
            val fileInputStream = FileInputStream(wavFile)
            fileInputStream.read(buffer, 0, headerSize)
            fileInputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

        // Verify the WAV file header
        val riffHeaderStr = String(buffer, 0, 4, Charset.forName("US-ASCII"))
        val formatHeaderStr = String(buffer, 8, 4, Charset.forName("US-ASCII"))

        return riffHeaderStr == "RIFF" && formatHeaderStr == "WAVE"
    }

    fun findErrorType(response: SpeechAPIResponse, errorType: ErrorType): List<String> {
        val mispronounced = mutableListOf<String>()

        if(response.nBest.isNullOrEmpty()) {
            return mispronounced;
        }

        val words = response.nBest[0].words;
        for (wordObject in words) {
            val assessment = wordObject.pronunciationAssessment;
            if(assessment.errorType != null && assessment.errorType == errorType.name) {
                mispronounced.add(wordObject.word)
            }
        }

        return mispronounced
    }
}