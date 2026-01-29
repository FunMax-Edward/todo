package com.edward.todov2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.edward.todov2.data.ActivityLog
import com.edward.todov2.data.AppDatabase
import com.edward.todov2.data.Problem
import com.edward.todov2.data.StudyUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class ProblemLabelFormat(val displayName: String) {
    UNIT_DASH("U1-12"),
    DECIMAL("1.12"),
    HASH("Unit1#12")
}

data class UnitOverviewState(
    val unit: StudyUnit,
    val levels: List<Int>
)

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).studyDao()

    val allUnits: Flow<List<StudyUnit>> = dao.getAllUnits()
    val allProblems: Flow<List<Problem>> = dao.getAllProblems()
    val allLogs: Flow<List<ActivityLog>> = dao.getAllLogs()

    val unitOverview: Flow<List<UnitOverviewState>> = combine(allUnits, allProblems) { units, problems ->
        units.map { unit ->
            UnitOverviewState(
                unit = unit,
                levels = problems.filter { it.unitId == unit.id }.map { it.level }
            )
        }
    }

    private val _labelFormat = MutableStateFlow(ProblemLabelFormat.UNIT_DASH)
    val labelFormat: StateFlow<ProblemLabelFormat> = _labelFormat.asStateFlow()

    fun updateLabelFormat(format: ProblemLabelFormat) {
        _labelFormat.value = format
    }

    fun formatProblemLabel(unit: StudyUnit?, problem: Problem, format: ProblemLabelFormat = _labelFormat.value): String {
        val unitName = unit?.name ?: "Unit"
        return when (format) {
            ProblemLabelFormat.UNIT_DASH -> "${unitName}-${problem.problemIndex}"
            ProblemLabelFormat.DECIMAL -> "${unit?.id ?: 0}.${problem.problemIndex}"
            ProblemLabelFormat.HASH -> "${unitName}#${problem.problemIndex}"
        }
    }

    fun getUnitById(unitId: Int): Flow<StudyUnit?> {
        return dao.getAllUnits().map { units -> units.find { it.id == unitId } }
    }

    fun getProblemsForUnit(unitId: Int): Flow<List<Problem>> = dao.getProblemsForUnit(unitId)

    fun addUnit(name: String, count: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val unitId = dao.insertUnit(StudyUnit(projectId = 0, name = name, problemCount = count)).toInt()
                val problems = (1..count).map {
                    Problem(unitId = unitId, problemIndex = it)
                }
                dao.insertProblems(problems)
            }
        }
    }

    fun addUnitsInBatch(definitions: List<Pair<String, Int>>) {
        if (definitions.isEmpty()) return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                definitions.forEach { (name, count) ->
                    if (name.isNotBlank() && count > 0) {
                        val unitId = dao.insertUnit(StudyUnit(projectId = 0, name = name.trim(), problemCount = count)).toInt()
                        val problems = (1..count).map { index ->
                            Problem(unitId = unitId, problemIndex = index)
                        }
                        dao.insertProblems(problems)
                    }
                }
            }
        }
    }

    fun markResult(problem: Problem, isCorrect: Boolean) {
        viewModelScope.launch {
            val newLevel = when {
                isCorrect -> when (problem.level) {
                    0 -> 5
                    1 -> 5
                    5 -> 5
                    else -> (problem.level - 1).coerceAtLeast(1)
                }
                else -> when (problem.level) {
                    0 -> 1
                    5 -> 1
                    4 -> 4
                    else -> problem.level + 1
                }
            }
            withContext(Dispatchers.IO) {
                dao.updateProblem(
                    problem.copy(
                        level = newLevel,
                        correctCount = if (isCorrect) problem.correctCount + 1 else problem.correctCount,
                        wrongCount = if (!isCorrect) problem.wrongCount + 1 else problem.wrongCount
                    )
                )
                dao.insertLog(
                    ActivityLog(
                        date = System.currentTimeMillis(),
                        problemId = problem.id,
                        isCorrect = isCorrect
                    )
                )
            }
        }
    }

    fun deleteUnit(unitId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.deleteProblemsForUnit(unitId)
                dao.deleteUnit(unitId)
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.deleteAllLogs()
                dao.deleteAllProblems()
                dao.deleteAllUnits()
            }
        }
    }
}
