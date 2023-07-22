package com.example.clarity.sets.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AndroidAudioRecorder(
    private val context: Context
) : AudioRecorder {
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private val bufferSizeInBytes: Int

    init {
        // Calculate the buffer size for AudioRecord based on the audio format and sampling rate
        val audioSource = MediaRecorder.AudioSource.MIC
        val sampleRate = 44100
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
    }

    override fun start(outputFile: File) {
        if (isRecording) return

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSizeInBytes
        )

        audioRecord?.startRecording()
        isRecording = true

        // Start a new thread to save the recorded data to a WAV file
        Thread(Runnable {
            saveWavFile(outputFile)
        }).start()
    }

    override fun stop() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    private fun saveWavFile(outputFile: File) {
        try {
            val outputStream = FileOutputStream(outputFile)
            val data = ByteArray(bufferSizeInBytes)
            val header = generateWavFileHeader()

            outputStream.write(header)

            while (isRecording) {
                val bytesRead = audioRecord?.read(data, 0, bufferSizeInBytes) ?: -1
                if (bytesRead != AudioRecord.ERROR_INVALID_OPERATION && bytesRead != AudioRecord.ERROR_BAD_VALUE) {
                    outputStream.write(data, 0, bytesRead)
                }
            }

            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun generateWavFileHeader(): ByteArray {
        // Construct the WAV file header
        // For simplicity, we assume 16-bit PCM audio, mono channel, 44100 Hz sample rate
        val totalAudioLen: Long = 0 // Placeholder, will be updated later
        val totalDataLen = totalAudioLen + 36
        val sampleRate = 44100
        val channels = 1
        val byteRate = (sampleRate * channels * 2).toLong()

        val header = ByteArrayOutputStream()
        val buffer = ByteBuffer.allocate(44)

        buffer.order(ByteOrder.LITTLE_ENDIAN)

        buffer.put("RIFF".toByteArray()) // Chunk ID
        buffer.putInt(totalDataLen.toInt()) // Chunk Size (total data length)
        buffer.put("WAVE".toByteArray()) // Format

        buffer.put("fmt ".toByteArray()) // Subchunk 1 ID
        buffer.putInt(16) // Subchunk 1 Size (PCM format size)
        buffer.putShort(1.toShort()) // Audio Format (PCM = 1)
        buffer.putShort(channels.toShort()) // Number of Channels
        buffer.putInt(sampleRate) // Sample Rate
        buffer.putInt(byteRate.toInt()) // Byte Rate
        buffer.putShort((channels * 2).toShort()) // Block Align
        buffer.putShort(16.toShort()) // Bits per Sample

        buffer.put("data".toByteArray()) // Subchunk 2 ID (data)
        buffer.putInt(0) // Subchunk 2 Size (total audio data length, placeholder)

        header.write(buffer.array())

        return header.toByteArray()
    }
}