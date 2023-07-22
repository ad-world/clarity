package com.example.clarity.sets

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.*
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.clarity.sdk.ClaritySDK
import com.example.clarity.R
import com.example.clarity.SessionManager
import com.example.clarity.sdk.CreateAttemptResponse
import com.example.clarity.sets.audio.AndroidAudioPlayer
import com.example.clarity.sets.audio.AndroidAudioRecorder
import com.example.clarity.sets.audio.PrevAndroidAudioRecorder
import com.example.clarity.sets.audio.WavPlayer
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.*
import java.io.File
import java.nio.ByteBuffer
import javazoom.jl.converter.Converter
import java.nio.ByteOrder
import java.nio.charset.Charset

class TestSetActivity() : AppCompatActivity() {


    private val recorder by lazy {
        AndroidAudioRecorder(applicationContext)
    }

    // TODO: Not sure if they can hear the correct recording after answering?
    //  added this here in case they can
    private val player by lazy {
        AndroidAudioPlayer(applicationContext)
    }

    private var audioFile: File? = null
    // private var player: TextToSpeech? = null

    private var isRecording = false
    private var recordingCompleted = false
    private val api = ClaritySDK().apiService
    private val sessionManager: SessionManager by lazy { SessionManager(this) }

    private val PERMISSIONS = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val PERMISSION_CODE = 123

    var userid = 0
    var index = 0
    var set: Set = Set(0, "", 0, mutableListOf<Card>(), 0, SetCategory.COMMUNITY_SET)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        setContentView(R.layout.activity_test_set)

        // Create TTS Object
        /*player = TextToSpeech(this, TextToSpeech.OnInitListener {
            if (it == TextToSpeech.SUCCESS) {
                player!!.language = Locale.ENGLISH
            } else {
                Log.d("TTS ERROR", it.toString())
            }
        })*/

        val intent = intent
        val setJson = intent.getStringExtra("set")
        val gson = Gson()
        set = gson.fromJson(setJson, Set::class.java)

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val iBtnClose = findViewById<ImageButton>(R.id.iBtnClose)
        val iBtnMic = findViewById<ImageButton>(R.id.iBtnMic)
        val iBtnSpeaker = findViewById<ImageButton>(R.id.iBtnSpeaker)
        val iBtnNext = findViewById<ImageButton>(R.id.iBtnNext)
        val cvPopUp = findViewById<CardView>(R.id.cvPopUp)
        val cvCompletedScreen = findViewById<CardView>(R.id.cvCompletedScreen)
        val btnReturn = findViewById<Button>(R.id.btnReturn)

        lifecycleScope.launch {
            userid = sessionManager.getUserId()
        }

        tvTitle.text = set.title
        iBtnClose.setOnClickListener {
            finish()
        }

        // Handle Speaker button click
        iBtnSpeaker.setOnClickListener {
            // player!!.speak(set.cards[index].phrase, TextToSpeech.QUEUE_ADD, null, null)
        }

        iBtnMic.setOnClickListener {
            if (!recordingCompleted) {
                if (!isRecording) {
                    // TODO: Change UI of button to reflect ongoing recording
                    //  ...
                    iBtnMic.setBackgroundResource(R.drawable.setclosebutton)
                    iBtnMic.setImageResource(R.drawable.baseline_mic_24_white)
                    File(cacheDir, "audio.wav").also {
                        recorder.start(it)
                        audioFile = it
                    }
                    /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE)
                    } else {
                        ClarityAudioRecorder.startRecording()
                    }*/
                } else {
                    // TODO Change UI of button to reflect recording stopped
                    //  ...
                    iBtnMic.setBackgroundResource(R.drawable.roundcorner)
                    iBtnMic.setImageResource(R.drawable.baseline_mic_24)
                    recorder.stop()
                    // ClarityAudioRecorder.stopRecording()
                    recordingCompleted = true

                    val score = getAccuracyScore(audioFile!!)
                    displayMessagePopup(score)
                    val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                    val tvCompletedCount = findViewById<TextView>(R.id.tvCompletedPhrases)
                    val tvPercentComplete = findViewById<TextView>(R.id.tvPercentComplete)
                    index++
                    progressBar.progress = (index * 100) / set.cards.size
                    tvCompletedCount.text = "$index Complete"
                    tvPercentComplete.text = "${(index * 100) / set.cards.size} %"
                    iBtnNext.visibility = VISIBLE
                    set.progress = index
                    iBtnMic.isEnabled = false
                }
                isRecording = !isRecording
            }
        }

        iBtnNext.setOnClickListener {
            iBtnMic.isEnabled = true
            if (index < set.cards.size) {
                iBtnNext.visibility = GONE
                cvPopUp.visibility = GONE
                loadCard(set.cards[index])
                recordingCompleted = false
            } else {
                cvCompletedScreen.visibility = VISIBLE
                iBtnClose.isEnabled = false
                iBtnNext.visibility = GONE
                iBtnMic.isEnabled = false
            }
        }

        btnReturn.setOnClickListener {
            cvCompletedScreen.visibility = GONE
            iBtnClose.isEnabled = true
            iBtnMic.isEnabled = true
            finish()
        }

        loadCard(set.cards[index])
    }

    private fun loadCard(card: Card) {
        val tvCardPhrase = findViewById<TextView>(R.id.tvCardPhrase)
        tvCardPhrase.text = card.phrase
    }

    private fun getAccuracyScore(wavFile: File): Int {
        // TODO: make this work later
        // val wavFile = convertMp3ToWav(file.absolutePath)
        // val wavFile = file
        // player.playFile(file)
        Log.d("has mic connected: ", packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE).toString())
        val wavPlayer = WavPlayer(applicationContext)
        //wavPlayer.playWavFileFromCache("audio.wav")

        Log.d("Wav File In Score: ", wavFile.toString())
        Log.d("Is Wav File In Score: ", isWavFile(wavFile).toString())
        Log.d("Does Wav file have data: ", hasWavData(wavFile).toString())
        val requestFile = RequestBody.create(MediaType.parse("audio/*"), wavFile)
        val part = MultipartBody.Part.createFormData("audio", wavFile.name, requestFile)
        val response : Response<CreateAttemptResponse> = runBlocking {
            return@runBlocking api.attemptCard(set.id, userid, set.cards[index].id, part)
        }
        // TODO: This currently fails with Error: 400, need to fix this
        Log.d("response: ", "$response")

        //Log.d("accuracy score: ", "${response.body()!!.metadata!!.accuracyScore}")
        //return response.body()!!.metadata!!.accuracyScore.toInt()
        return 100
    }

    @SuppressLint("SetTextI18n")
    private fun displayMessagePopup(score: Int)  {
        val cvPopUp = findViewById<CardView>(R.id.cvPopUp)
        val tvResultMessage = findViewById<TextView>(R.id.tvResultMessage)
        if (score in 0..50)  {
            cvPopUp.setCardBackgroundColor(Color.YELLOW)
            tvResultMessage.text = resources.getString(R.string.try_again)
        } else if (score in 51..100) {
            cvPopUp.setCardBackgroundColor(Color.GREEN)
            tvResultMessage.text = resources.getString(R.string.great_job)
        }

        cvPopUp.visibility = VISIBLE
        // Log.d("Tag Visibility 1", cvPopUp.visibility.toString())
    }

    /*
    private fun convertMp3ToWav(mp3File: File): File {
        // Create a new WAV file
        val wavFile = File(cacheDir, "audio.wav")
        wavFile.createNewFile()

        try {
            // Initialize the MediaCodec
            val mediaCodec = MediaCodec.createDecoderByType("audio/mpeg")
            val mediaFormat = MediaFormat.createAudioFormat("audio/mpeg", 44100, 2)
            mediaCodec.configure(mediaFormat, null, null, 0)
            mediaCodec.start()

            // Input and output streams
            val inputStream = FileInputStream(mp3File)
            val outputStream = FileOutputStream(wavFile)

            val codecInputBufferIndex = mediaCodec.dequeueInputBuffer(-1)
            val codecOutputBufferIndex = mediaCodec.dequeueOutputBuffer(MediaCodec.BufferInfo(), -1)
            val codecInputBuffer = mediaCodec.getInputBuffer(codecInputBufferIndex)
            val codecOutputBuffer = mediaCodec.getOutputBuffer(codecOutputBufferIndex)

            // Buffer size
            val bufferSize = 4096
            val buffer = ByteArray(bufferSize)

            // Decode MP3 and write to WAV
            while (true) {
                val bytesRead = inputStream.read(buffer)
                if (bytesRead == -1) break

                codecInputBuffer?.put(buffer, 0, bytesRead)
                mediaCodec.queueInputBuffer(codecInputBufferIndex, 0, bytesRead, 0, 0)
                val info = MediaCodec.BufferInfo()
                mediaCodec.dequeueOutputBuffer(info, -1)

                val chunkSize = info.size
                val chunkData = ByteArray(chunkSize)
                codecOutputBuffer?.get(chunkData)

                outputStream.write(chunkData, 0, chunkSize)
                mediaCodec.releaseOutputBuffer(codecOutputBufferIndex, false)
            }

            // Release resources
            mediaCodec.stop()
            mediaCodec.release()
            inputStream.close()
            outputStream.close()

        } catch (e: IOException) {
            Log.d("AN ERROR HAS OCCURRED", "1")
            e.printStackTrace()
        }

        // Return file
        return wavFile
    }


    private fun convertMp3ToWav(mp3FilePath: String): File? {
        try {
            // Initialize the MediaPlayer
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(mp3FilePath)
            mediaPlayer.prepare()

            // Create a new WAV file
            val wavFile = File(cacheDir, "audio.wav")
            wavFile.createNewFile()


            // Initialize the FileOutputStream for the WAV file
            val outputStream = FileOutputStream(wavFile)

            // Get the duration of the MP3 audio in milliseconds
            val durationInMillis = mediaPlayer.duration

            // Set up the MediaCodec to encode WAV
            val mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_RAW)
            val mediaFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_RAW, 44100, 2)
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 128000) // Adjust bit rate as needed
            mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            mediaCodec.start()

            // Get the input and output buffers for MediaCodec
            val inputBufferIndex = mediaCodec.dequeueInputBuffer(-1)
            val inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex)
            val outputBufferIndex = mediaCodec.dequeueOutputBuffer(MediaCodec.BufferInfo(), -1)
            val outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex)

            // Start encoding
            var presentationTimeUs: Long = 0
            var offset = 0
            var size: Int

            while (true) {
                // Read data from MediaPlayer
                val bytesRead = mediaPlayer.readBytes(inputBuffer)

                if (bytesRead <= 0) {
                    break // End of stream
                }

                size = bytesRead
                inputBuffer.position(0)
                inputBuffer.limit(size)

                // Encode the PCM data to WAV format
                val inputBufferInfo = MediaCodec.BufferInfo()
                inputBufferInfo.set(0, size, presentationTimeUs, 0)
                mediaCodec.queueInputBuffer(inputBufferIndex, 0, size, presentationTimeUs, 0)

                // Get the encoded WAV data from MediaCodec
                var outputBufferIndex = mediaCodec.dequeueOutputBuffer(inputBufferInfo, -1)
                while (outputBufferIndex >= 0) {
                    // Write the WAV data to the output file
                    val outputData = ByteArray(inputBufferInfo.size)
                    outputBuffer?.get(outputData)
                    outputStream.write(outputData)

                    // Release the output buffer
                    mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
                    outputBufferIndex = mediaCodec.dequeueOutputBuffer(inputBufferInfo, 0)
                }

                presentationTimeUs += 1000000 * bytesRead / (2 * 44100)
            }

            // Stop and release resources
            mediaPlayer.stop()
            mediaPlayer.release()

            mediaCodec.stop()
            mediaCodec.release()
            outputStream.close()
            return wavFile
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }*/


    /*
    fun convertMp3ToWav(mp3FilePath: String): File? {
        val bufferSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT)
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
            .setAudioFormat(AudioFormat.Builder().setSampleRate(44100).setChannelMask(AudioFormat.CHANNEL_OUT_STEREO).setEncoding(AudioFormat.ENCODING_PCM_16BIT).build())
            .setBufferSizeInBytes(bufferSize)
            .build()

        val extractor = MediaExtractor()
        try {
            extractor.setDataSource(mp3FilePath)

            // Find and select the audio track
            var audioTrackIndex = -1
            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                if (mime?.startsWith("audio/") == true) {
                    audioTrackIndex = i
                    break
                }
            }
            if (audioTrackIndex == -1) {
                throw RuntimeException("No audio track found in the MP3 file.")
            }

            extractor.selectTrack(audioTrackIndex)

            // Create a new WAV file
            val wavFile = File(cacheDir, "audio.wav")
            wavFile.createNewFile()

            // Initialize the FileOutputStream for the WAV file
            val outputStream = FileOutputStream(wavFile)

            // Read the audio data from the MP3 file and write it to the WAV file
            val buffer = ByteBuffer.allocate(bufferSize)
            while (true) {
                buffer.clear()
                val sampleSize = extractor.readSampleData(buffer, 0)
                if (sampleSize < 0) {
                    break // End of stream
                }

                audioTrack.write(buffer.array(), 0, sampleSize)
                outputStream.write(buffer.array(), 0, sampleSize)

                extractor.advance()
            }

            // Release resources
            extractor.release()
            audioTrack.release()
            outputStream.close()

            return wavFile

        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }*/

    /*
    fun convertMp3ToWav(mp3FilePath: String): File? {
        try {
            val wavFile = File(cacheDir, "audio.wav")
            wavFile.createNewFile()

            val extractor = MediaExtractor()
            extractor.setDataSource(mp3FilePath)

            // Find and select the audio track
            var audioTrackIndex = -1
            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                if (mime?.startsWith("audio/") == true) {
                    audioTrackIndex = i
                    break
                }
            }

            if (audioTrackIndex == -1) {
                throw RuntimeException("No audio track found in the MP3 file.")
            }

            extractor.selectTrack(audioTrackIndex)

            // Get the audio format
            val audioFormat = extractor.getTrackFormat(audioTrackIndex)

            // Create a new WAV file with the correct audio format
            val outputStream = FileOutputStream(wavFile)
            outputStream.write(generateWavFileHeader(audioFormat))

            val bufferSize = 4096
            val buffer = ByteBuffer.allocate(bufferSize)

            while (true) {
                buffer.clear()
                val sampleSize = extractor.readSampleData(buffer, 0)
                if (sampleSize < 0) {
                    break // End of stream
                }
                outputStream.write(buffer.array(), 0, sampleSize)
                extractor.advance()
            }

            // Update the WAV file header with the correct data size
            outputStream.close()
            updateWavFileHeader(wavFile)

            // Release resources
            extractor.release()

            return wavFile
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    private fun generateWavFileHeader(audioFormat: MediaFormat): ByteArray {
        val channels = audioFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
        val sampleRate = audioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)
        val bitsPerSample = 16 // We're assuming 16-bit PCM audio

        val byteArray = ByteArray(44)
        val buffer = ByteBuffer.wrap(byteArray)

        // RIFF header
        buffer.put("RIFF".toByteArray()) // Chunk ID
        buffer.putInt(0) // Chunk Size (placeholder)
        buffer.put("WAVE".toByteArray()) // Format

        // Format Subchunk
        buffer.put("fmt ".toByteArray()) // Subchunk 1 ID
        buffer.putInt(16) // Subchunk 1 Size (PCM format size)
        buffer.putShort(1.toShort()) // Audio Format (PCM = 1)
        buffer.putShort(channels.toShort()) // Number of Channels
        buffer.putInt(sampleRate) // Sample Rate
        buffer.putInt(sampleRate * channels * bitsPerSample / 8) // Byte Rate
        buffer.putShort((channels * bitsPerSample / 8).toShort()) // Block Align
        buffer.putShort(bitsPerSample.toShort()) // Bits per Sample

        // Data Subchunk
        buffer.put("data".toByteArray()) // Subchunk 2 ID
        buffer.putInt(0) // Subchunk 2 Size (placeholder)

        return byteArray
    }

    private fun updateWavFileHeader(wavFile: File) {
        val fileSizeInBytes = wavFile.length()
        val headerSize = 44 // The WAV header size is 44 bytes

        val header = ByteBuffer.allocate(headerSize)
        val outputStream = FileOutputStream(wavFile)

        // Update Subchunk 2 Size field in the header with the correct data size
        header.order(ByteOrder.LITTLE_ENDIAN)
        header.putInt(40, (fileSizeInBytes - headerSize + 8).toInt())

        outputStream.write(header.array())
        outputStream.close()
    }*/

    fun convertMp3ToWav(mp3FilePath: String): File? {
        return try {
            val wavFile = File(cacheDir, "audio.wav")
            wavFile.createNewFile()

            val converter = Converter()
            converter.convert(mp3FilePath, wavFile.absolutePath)

            wavFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun isWavFile(wavFile: File): Boolean {
        if (!wavFile.exists() || wavFile == null) {
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
        Log.d("riffHeader", riffHeaderStr)
        Log.d("formatHeader", formatHeaderStr)

        return riffHeaderStr == "RIFF" && formatHeaderStr == "WAVE"
    }

    fun hasWavData(wavFile: File): Boolean {
        if (!wavFile.exists() || !wavFile.isFile()) {
            return false
        }

        try {
            val fileInputStream = FileInputStream(wavFile)

            // Skip the WAV header (first 44 bytes)
            val headerSize = 44
            fileInputStream.skip(headerSize.toLong())

            // Read the data chunk size (4 bytes, little-endian)
            val dataSizeBuffer = ByteArray(4)
            fileInputStream.read(dataSizeBuffer)

            // Convert the 4 bytes to an integer (little-endian)
            val dataSize = ByteBuffer.wrap(dataSizeBuffer).order(ByteOrder.LITTLE_ENDIAN).int

            fileInputStream.close()

            // If the dataSize is greater than zero, the WAV file contains audio data
            return dataSize > 0
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return false
    }
}