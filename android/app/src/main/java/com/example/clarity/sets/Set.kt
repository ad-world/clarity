package com.example.clarity.sets

data class Set(
    val id: Int,
    val title: String,
    val userId: Int,
    val cards: MutableList<Card>,
    var progress: Int,
    val category:SetCategory,
)