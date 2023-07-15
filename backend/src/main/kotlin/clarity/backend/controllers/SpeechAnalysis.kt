package clarity.backend.controllers

import SpeechAPIResponse
import TemporaryFileStorage
import com.google.gson.Gson
import com.microsoft.cognitiveservices.speech.*
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import org.springframework.web.multipart.MultipartFile
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
            println("speechKey is: ${speechKey}")
            println("speechRegion: ${speechRegion}")
            speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion)
        } catch (e: Exception) {
            println("catching exception here")
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
            speechConfig?.setProperty(PropertyId.SpeechServiceConnection_InitialSilenceTimeoutMs, "5000")
            speechConfig?.setProperty(PropertyId.SpeechServiceConnection_EndSilenceTimeoutMs, "5000");

            val speechRecognizer = SpeechRecognizer(speechConfig, config)
            val assessmentConfig = PronunciationAssessmentConfig(
                phrase,
                PronunciationAssessmentGradingSystem.HundredMark,
                PronunciationAssessmentGranularity.Phoneme,
                true
            )

            assessmentConfig.applyTo(speechRecognizer)

            val future = speechRecognizer.recognizeOnceAsync();
            val result = future.get(10, TimeUnit.SECONDS);

            val pronunciationAssessmentResult = PronunciationAssessmentResult.fromResult(result)
            val pronunciationAssessmentResultJson = result.properties.getProperty(PropertyId.SpeechServiceResponse_JsonResult);

            val gson = Gson()
            val parsedResponse: SpeechAPIResponse = gson.fromJson(pronunciationAssessmentResultJson, SpeechAPIResponse::class.java)

            speechRecognizer.close()
            speechConfig?.close()
            config.close()
            assessmentConfig.close()
            result.close()

            tempFile.deleteFile(filePath)

            return SpeechAnalysisResult(json = parsedResponse, assessmentResult = pronunciationAssessmentResult)
        } catch (e: Exception) {
            e.printStackTrace();
            return null;
        }
    }
}