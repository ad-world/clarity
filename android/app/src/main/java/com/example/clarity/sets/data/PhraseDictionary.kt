package com.example.clarity.sets.data

// TODO: this is here as a reminder to implement it later
data class PhraseDictionary(
    val phrases: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PhraseDictionary

        if (!phrases.contentEquals(other.phrases)) return false

        return true
    }

    override fun hashCode(): Int {
        return phrases.contentHashCode()
    }
}
