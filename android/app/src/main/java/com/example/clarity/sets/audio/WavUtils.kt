package com.example.clarity.sets.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaExtractor
import android.media.MediaFormat
import java.io.*
import java.nio.ByteBuffer

object WavUtils {
    fun convertMp3ToWav(mp3FilePath: String, wavFilePath: String) {
        try {
            // Step 1: Extract audio format information using MediaExtractor
            val extractor = MediaExtractor()
            extractor.setDataSource(mp3FilePath)

            val audioFormat = extractor.getTrackFormat(0)
            val sampleRate = audioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            val channels = audioFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT).toShort()

            // Step 2: Convert the MP3 data to PCM using AudioTrack
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            val data = ByteArray(bufferSize)
            val outputStream = ByteArrayOutputStream()

            val audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM
            )

            audioTrack.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                override fun onPeriodicNotification(track: AudioTrack) {}

                override fun onMarkerReached(track: AudioTrack) {
                    // Write the converted PCM data to the output stream
                    outputStream.write(data)
                }
            })

            audioTrack.positionNotificationPeriod = bufferSize
            audioTrack.play()

            // Step 3: Write the PCM data to a WAV file
            val outputWavFile = File(wavFilePath)
            val outputStreamWav = DataOutputStream(BufferedOutputStream(FileOutputStream(outputWavFile)))

            // Calculate the total audio length in bytes
            val totalAudioLen = extractor.getSampleTime() * 2 // 2 bytes per sample for 16-bit PCM audio

            // Write the WAV file header using the correct totalAudioLen
            outputStreamWav.write(createWavFileHeader(sampleRate, totalAudioLen, channels, bufferSize.toLong()))

            // Rest of the code ...
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createWavFileHeader(sampleRate: Int, totalAudioLen: Long, channels: Short, byteRate: Long): ByteArray {
        val headerSize = 44
        val header = ByteArray(headerSize)

        // RIFF/WAVE header
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()

        // Total file size (data size + 36 bytes for the header)
        val totalDataLen = totalAudioLen + 36
        header[4] = (totalDataLen and 0xFF).toByte()
        header[5] = (totalDataLen shr 8 and 0xFF).toByte()
        header[6] = (totalDataLen shr 16 and 0xFF).toByte()
        header[7] = (totalDataLen shr 24 and 0xFF).toByte()

        // WAVE header
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()

        // Format chunk "fmt " sub-chunk
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()

        // Format chunk length (16 bytes for PCM)
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0

        // Audio format (PCM = 1)
        header[20] = 1
        header[21] = 0

        // Number of channels
        header[22] = channels.toByte()
        header[23] = 0

        // Sample rate
        header[24] = (sampleRate and 0xFF).toByte()
        header[25] = (sampleRate shr 8 and 0xFF).toByte()
        header[26] = (sampleRate shr 16 and 0xFF).toByte()
        header[27] = (sampleRate shr 24 and 0xFF).toByte()

        // Byte rate (SampleRate * NumChannels * BitsPerSample/8)
        val blockAlign = (channels * 16 / 8).toShort()
        val byteRateCalc = sampleRate.toLong() * blockAlign
        header[28] = (byteRateCalc and 0xFF).toByte()
        header[29] = (byteRateCalc shr 8 and 0xFF).toByte()
        header[30] = (byteRateCalc shr 16 and 0xFF).toByte()
        header[31] = (byteRateCalc shr 24 and 0xFF).toByte()

        // Block align (NumChannels * BitsPerSample/8)
        header[32] = blockAlign.toByte()
        header[33] = 0

        // Bits per sample (16 for PCM)
        header[34] = 16
        header[35] = 0

        // Data chunk "data" sub-chunk
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()

        // Data size (totalAudioLen)
        header[40] = (totalAudioLen and 0xFF).toByte()
        header[41] = (totalAudioLen shr 8 and 0xFF).toByte()
        header[42] = (totalAudioLen shr 16 and 0xFF).toByte()
        header[43] = (totalAudioLen shr 24 and 0xFF).toByte()

        return header
    }
}