package com.example.clarity.sets.audio

import android.content.Context
import android.media.MediaPlayer
import java.io.File
import java.io.FileInputStream

class WavPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun playWavFileFromCache(cacheFilePath: String) {
        try {
            // If MediaPlayer is already playing a file, stop it first
            stopPlaying()

            // Create a new MediaPlayer instance
            mediaPlayer = MediaPlayer()

            // Set the data source to the specified WAV file in the cache directory
            val cacheFile = File(context.cacheDir, cacheFilePath)
            val fileInputStream = FileInputStream(cacheFile)
            mediaPlayer?.setDataSource(fileInputStream.fd)

            // Prepare the MediaPlayer asynchronously
            mediaPlayer?.prepareAsync()

            // Set a listener to start playing the WAV file once it's prepared
            mediaPlayer?.setOnPreparedListener { mp ->
                mp.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopPlaying() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}