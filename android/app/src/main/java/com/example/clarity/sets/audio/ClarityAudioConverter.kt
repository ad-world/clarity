package com.example.clarity.sets.audio

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

class ClarityAudioConverter {
    fun convertMp4ToWav(mp4FilePath: String, wavFilePath: String) {
        val mediaExtractor = MediaExtractor()
        mediaExtractor.setDataSource(mp4FilePath)

        var audioTrackIndex = -1
        var mediaFormat: MediaFormat? = null

        // Find the audio track in the MP4 file
        for (i in 0 until mediaExtractor.trackCount) {
            val format = mediaExtractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("audio/") == true) {
                audioTrackIndex = i
                mediaFormat = format
                break
            }
        }

        if (audioTrackIndex == -1 || mediaFormat == null) {
            // Audio track not found
            return
        }

        mediaExtractor.selectTrack(audioTrackIndex)

        val mediaCodec = MediaCodec.createDecoderByType(mediaFormat.getString(MediaFormat.KEY_MIME)!!)
        mediaCodec.configure(mediaFormat, null, null, 0)
        mediaCodec.start()

        val outputFile = File(wavFilePath)
        val outputStream = FileOutputStream(outputFile)

        val bufferInfo = MediaCodec.BufferInfo()
        val byteBuffer = ByteBuffer.allocate(4096)

        outputStream.write(createWavHeader(mediaFormat))

        while (true) {
            val inputBufferIndex = mediaCodec.dequeueInputBuffer(10000)
            if (inputBufferIndex >= 0) {
                val inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex)
                val sampleSize = mediaExtractor.readSampleData(inputBuffer!!, 0)
                if (sampleSize < 0) {
                    break
                }
                mediaCodec.queueInputBuffer(inputBufferIndex, 0, sampleSize, mediaExtractor.sampleTime, 0)
                mediaExtractor.advance()
            }

            val outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000)
            if (outputBufferIndex >= 0) {
                val outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex)
                outputBuffer?.get(byteBuffer.array())
                outputStream.write(byteBuffer.array(), 0, bufferInfo.size)
                mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
            }

            if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                break
            }
        }

        mediaCodec.stop()
        mediaCodec.release()
        mediaExtractor.release()
        outputStream.close()
    }

    private fun createWavHeader(mediaFormat: MediaFormat): ByteArray {
        val sampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)
        val channelCount = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
        val byteRate = sampleRate * channelCount * 2

        val header = ByteArray(44)
        header[0] = 'R'.code.toByte() // RIFF/WAVE header
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (byteRate + 36 and 0xff).toByte() // File size - 8
        header[5] = (byteRate + 36 shr 8 and 0xff).toByte()
        header[6] = (byteRate + 36 shr 16 and 0xff).toByte()
        header[7] = (byteRate + 36 shr 24 and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte() // 'fmt ' chunk
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16 // 4 bytes: size of 'fmt ' chunk
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // format = 1 (PCM)
        header[21] = 0
        header[22] = channelCount.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = (sampleRate shr 8 and 0xff).toByte()
        header[26] = (sampleRate shr 16 and 0xff).toByte()
        header[27] = (sampleRate shr 24 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        header[32] = (2 * 16 / 8).toByte() // block align
        header[33] = 0
        header[34] = 16 // bits per sample
        header[35] = 0
        header[36] = 'd'.code.toByte() // 'data' chunk
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (mediaFormat.getLong(MediaFormat.KEY_DURATION) and 0xff).toByte() // data size
        header[41] = (mediaFormat.getLong(MediaFormat.KEY_DURATION) shr 8 and 0xff).toByte()
        header[42] = (mediaFormat.getLong(MediaFormat.KEY_DURATION) shr 16 and 0xff).toByte()
        header[43] = (mediaFormat.getLong(MediaFormat.KEY_DURATION) shr 24 and 0xff).toByte()

        return header
    }
}