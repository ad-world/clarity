package clarity.backend.controllers

import TemporaryFileStorage
import com.microsoft.cognitiveservices.speech.*
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.TimeUnit

class SpeechController {
    private val speechKey = "3b3d4c713bac4682a81cc3a02976ea6e"
    private val speechReigon = "eastus"
    private val speechConfig = SpeechConfig.fromSubscription(speechKey, speechReigon)

    fun analyzeAudio(file: MultipartFile, phrase: String): String? {
        try {
            val tempFile = TemporaryFileStorage();
            val filePath = tempFile.storeFile(file)
            val config: AudioConfig = AudioConfig.fromWavFileInput(filePath)
            speechConfig.setProperty(PropertyId.SpeechServiceConnection_InitialSilenceTimeoutMs, "5000")
            speechConfig.setProperty(PropertyId.SpeechServiceConnection_EndSilenceTimeoutMs, "5000");

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

            val json = result.properties.getProperty(PropertyId.SpeechServiceResponse_JsonResult);

            speechRecognizer.close()
            speechConfig.close()
            config.close()
            assessmentConfig.close()
            result.close()

            tempFile.deleteFile(filePath)

            return json
        } catch (e: Exception) {
            e.printStackTrace();
            return null;
        }

    }

}