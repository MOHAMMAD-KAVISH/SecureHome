package com.example.securehomeplus.utils

import com.example.securehomeplus.data.database.entities.SecurityFactor
import com.example.securehomeplus.viewmodel.EvaluationResult
import kotlin.math.roundToInt

object SecurityEvaluator {
    fun calculateScore(questions: List<SecurityFactor>, answers: Map<Int, Boolean>): EvaluationResult {
        if (questions.isEmpty()) return EvaluationResult(0, emptyList(), emptyList())

        val totalWeight = questions.sumOf { it.weight }
        var obtained = 0
        val failedIds = mutableListOf<Int>()
        val recs = mutableListOf<String>()

        questions.forEach { q ->
            val ok = answers[q.id] ?: false
            if (ok) {
                obtained += q.weight
            } else {
                failedIds.add(q.id)

                when {
                    q.question.contains("door", ignoreCase = true) -> recs.add("Ensure all doors are locked and install deadbolt locks.")
                    q.question.contains("cctv", ignoreCase = true) -> recs.add("Install CCTV cameras at main entrances for continuous monitoring.")
                    q.question.contains("fire extinguisher", ignoreCase = true) -> recs.add("Keep a functional fire extinguisher on each floor and check expiry.")
                    q.question.contains("window", ignoreCase = true) -> recs.add("Secure windows with grills or window locks.")
                    q.question.contains("light", ignoreCase = true) -> recs.add("Add motion-sensor lights at entry points.")
                    q.question.contains("contact", ignoreCase = true) -> recs.add("Keep emergency contacts visible near phone.")
                    q.question.contains("gas", ignoreCase = true) -> recs.add("Label and maintain gas valves; shut off when not in use.")
                    q.question.contains("smoke", ignoreCase = true) -> recs.add("Test smoke detectors monthly and replace batteries.")
                    else -> recs.add("Review: ${q.question} — take corrective action.")
                }
            }
        }

        val percent = if (totalWeight == 0) 0 else ((obtained.toDouble() / totalWeight.toDouble()) * 100.0).roundToInt()
        return EvaluationResult(percent, recs.distinct(), failedIds)
    }
}
