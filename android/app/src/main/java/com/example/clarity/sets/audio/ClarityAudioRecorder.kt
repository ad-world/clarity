package com.example.clarity.sets.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object ClarityAudioRecorder {
    private const val SAMPLE_RATE = 44100
    private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT

    private var recordingThread: Thread? = null
    private var isRecording = false

    fun startRecording() {
        if (isRecording) {
            return
        }

        recordingThread = Thread(Runnable {
            isRecording = true
            recordAudio()
        })

        recordingThread?.start()
    }

    fun stopRecording() {
        isRecording = false
    }

    @SuppressLint("MissingPermission")
    private fun recordAudio() {
        val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)

        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            bufferSize
        )

        val audioData = ByteArray(bufferSize)
        val outputFile = getOutputMediaFile()

        audioRecord.startRecording()
        val fileOutputStream = FileOutputStream(outputFile)

        try {
            while (isRecording) {
                val bytesRead = audioRecord.read(audioData, 0, bufferSize)
                if (bytesRead > 0) {
                    fileOutputStream.write(audioData, 0, bytesRead)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            audioRecord.stop()
            audioRecord.release()
            fileOutputStream.close()
        }
    }

    fun getOutputMediaFile(): File {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
            "MyAudioRecordings"
        )

        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs()
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File("${mediaStorageDir.absolutePath}${File.separator}REC_${timeStamp}.wav")
    }
}