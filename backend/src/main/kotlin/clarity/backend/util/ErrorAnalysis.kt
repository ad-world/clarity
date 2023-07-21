package clarity.backend.util

import com.microsoft.cognitiveservices.speech.PronunciationAssessmentResult

enum class ErrorType {
    Mispronunciation,
    Insertion,
    Omission
}

enum class Difficulty {
    Easy,
    Medium,
    Hard
}


// Factory Design Pattern
interface ErrorAnalysis {
    fun shouldCompleteCard(assessment: PronunciationAssessmentResult, omissions: List<String>, mispronunciations: List<String>, insertions: List<String>): Boolean
}

class EasyErrorAnalysis: ErrorAnalysis {
    override fun shouldCompleteCard(
        assessment: PronunciationAssessmentResult,
        omissions: List<String>,
        mispronunciations: List<String>,
        insertions: List<String>
    ): Boolean {
        val completeness = assessment.completenessScore
        val accuracy = assessment.accuracyScore
        val fluency = assessment.fluencyScore
        val pronunciation = assessment.pronunciationScore

        var shouldComplete = true

        if(completeness < 70) shouldComplete = false;
        if(accuracy < 60) shouldComplete = false;
        if(fluency < 60) shouldComplete = false;
        if(pronunciation < 70) shouldComplete = false;

        if(omissions.size > 1) shouldComplete = false;
        if(mispronunciations.size > 1) shouldComplete = false;
        if(insertions.isNotEmpty()) shouldComplete = false;



        return shouldComplete
    }
}

class MediumErrorAnalysis: ErrorAnalysis {
    override fun shouldCompleteCard(
        assessment: PronunciationAssessmentResult,
        omissions: List<String>,
        mispronunciations: List<String>,
        insertions: List<String>
    ): Boolean {
        val completeness = assessment.completenessScore
        val accuracy = assessment.accuracyScore
        val fluency = assessment.fluencyScore
        val pronunciation = assessment.pronunciationScore

        var shouldComplete = true

        if(completeness < 70) shouldComplete = false;
        if(accuracy < 85) shouldComplete = false;
        if(fluency < 80) shouldComplete = false;
        if(pronunciation < 85) shouldComplete = false;

        if(omissions.size > 1) shouldComplete = false;
        if(mispronunciations.size > 1) shouldComplete = false;
        if(insertions.isNotEmpty()) shouldComplete = false;

        return shouldComplete
    }
}

class HardErrorAnalysis: ErrorAnalysis {
    override fun shouldCompleteCard(
        assessment: PronunciationAssessmentResult,
        omissions: List<String>,
        mispronunciations: List<String>,
        insertions: List<String>
    ): Boolean {
        val completeness = assessment.completenessScore
        val accuracy = assessment.accuracyScore
        val fluency = assessment.fluencyScore
        val pronunciation = assessment.pronunciationScore

        var shouldComplete = true

        if(completeness < 90) shouldComplete = false;
        if(accuracy < 90) shouldComplete = false;
        if(fluency < 90) shouldComplete = false;
        if(pronunciation < 90) shouldComplete = false;

        if(omissions.size > 1) shouldComplete = false;
        if(mispronunciations.size > 1) shouldComplete = false;
        if(insertions.isNotEmpty()) shouldComplete = false;

        return shouldComplete
    }
}