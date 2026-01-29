package com.edward.todov2.domain.usecase

import com.edward.todov2.data.Problem
import com.edward.todov2.data.ActivityLog
import com.edward.todov2.domain.repository.StudyRepository

/**
 * Use case for updating problem proficiency level
 * Encapsulates the business logic for level transitions
 */
class UpdateProblemProficiencyUseCase(
    private val repository: StudyRepository
) {
    
    /**
     * Mark a problem as correct or incorrect and update its level
     * according to the proficiency state machine
     */
    suspend operator fun invoke(problem: Problem, isCorrect: Boolean) {
        val newLevel = calculateNewLevel(problem.level, isCorrect)
        
        val updatedProblem = problem.copy(
            level = newLevel,
            correctCount = if (isCorrect) problem.correctCount + 1 else problem.correctCount,
            wrongCount = if (!isCorrect) problem.wrongCount + 1 else problem.wrongCount
        )
        
        repository.updateProblem(updatedProblem)
        
        repository.insertLog(
            ActivityLog(
                date = System.currentTimeMillis(),
                problemId = problem.id,
                isCorrect = isCorrect
            )
        )
    }
    
    /**
     * State machine for proficiency levels:
     * Level 0 (Grey - Never attempted)
     * Level 1-4 (Reds - Increasing difficulty)
     * Level 5 (Green - Mastered)
     */
    private fun calculateNewLevel(currentLevel: Int, isCorrect: Boolean): Int {
        return when {
            isCorrect -> when (currentLevel) {
                0 -> 5  // First attempt correct -> Mastered
                1 -> 5  // Light red + correct -> Mastered
                5 -> 5  // Already mastered, stay mastered
                else -> (currentLevel - 1).coerceAtLeast(1)  // Move towards mastery
            }
            else -> when (currentLevel) {
                0 -> 1  // First attempt wrong -> Light red
                5 -> 1  // Mastered + wrong -> Light red (forgot)
                4 -> 4  // Darkest red, cap at this level
                else -> currentLevel + 1  // Increase difficulty
            }
        }
    }
}
