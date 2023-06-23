package com.example.clarity.setspage

data class Set(
    val title: String,
    val cardCount: Int,
    val cards: MutableList<Card>,
    val progress: Float,
)