package com.example.clarity.sets

data class Set(
    val title: String,
    val cardCount: Int,
    val cards: MutableList<Card>,
    val progress: Double,
    val category:SetCategory,
)

enum class SetCategory {
    MYSET, DEFAULTSET, SAVEDSET, BOOKMARKEDSET,
}