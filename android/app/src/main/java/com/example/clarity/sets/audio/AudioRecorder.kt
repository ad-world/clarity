package com.example.clarity.sets.audio

import java.io.File

interface AudioRecorder {
    fun start(outputFile: File)
    fun stop()
}