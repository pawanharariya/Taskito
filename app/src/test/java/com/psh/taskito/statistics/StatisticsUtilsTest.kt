package com.psh.taskito.statistics

import com.psh.taskito.data.Task
import org.junit.Assert.assertEquals
import org.junit.Test

class StatisticsUtilsTest {

    @Test
    fun getActiveAndCompletedStats_noCompleted_returnsHundredZero() {
        val tasks = listOf(
            Task("title", "description", false)
        )

        val result = getActiveAndCompletedStats(tasks)
        assertEquals(100f, result.activeTasksPercent)
        assertEquals(0f, result.completedTasksPercent)
    }

    @Test
    fun getActiveAndCompletedStats_AllCompleted_returnsZeroHundred() {
        val tasks = listOf(
            Task("title", "description", true)
        )

        val result = getActiveAndCompletedStats(tasks)
        assertEquals(100f, result.completedTasksPercent)
        assertEquals(0f, result.activeTasksPercent)
    }

    @Test
    fun getActiveAndCompletedStats_HalfCompleted_returnsFiftyFifty() {
        val tasks = listOf(
            Task("title", "description", false),
            Task("title", "description", false),
            Task("title", "description", true),
            Task("title", "description", true)
        )

        val result = getActiveAndCompletedStats(tasks)
        assertEquals(50f, result.completedTasksPercent)
        assertEquals(50f, result.activeTasksPercent)
    }

    @Test
    fun getActiveAndCompletedStats_empty_returnsZeroes() {
        val tasks = listOf<Task>()
        val result = getActiveAndCompletedStats(tasks)
        assertEquals(0f, result.completedTasksPercent)
        assertEquals(0f, result.activeTasksPercent)
    }

    @Test
    fun getActiveAndCompletedStats_error_returnsZeroes() {
        val tasks = null
        val result = getActiveAndCompletedStats(tasks)
        assertEquals(0f, result.completedTasksPercent)
        assertEquals(0f, result.activeTasksPercent)
    }

}