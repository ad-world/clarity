package com.example.clarity.sets.audio

import android.content.Context
import android.media.MediaRecorder
import java.io.File
import java.io.FileOutputStream

class PrevAndroidAudioRecorder(
    private val context: Context
): AudioRecorder {
    private var recorder: MediaRecorder? = null

    private fun createRecorder(): MediaRecorder {
        return MediaRecorder(context)
    }

    override fun start(outputFile: File) {
        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            //TODO: currently MP3 (maybe use a different file)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(FileOutputStream(outputFile).fd)

            prepare()
            start()

            recorder = this
        }
    }

    override fun stop() {
        recorder?.stop()
        recorder?.reset()
        recorder = null
    }
}