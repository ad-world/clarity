package com.example.clarity.sets.audio

import java.io.File

interface AudioPlayer {
    fun playFile(file: File)
    fun stop()
}