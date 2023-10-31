package com.psh.taskito.statistics

import com.psh.taskito.data.Task

/**
 * Function that does some trivial computation. Used to showcase unit tests.
 */
internal fun getActiveAndCompletedStats(tasks: List<Task>?): StatsResult {
    val totalTasks = tasks!!.size
    val numberOfActiveTasks = tasks.count { it.isActive }
    return StatsResult(
        activeTasksPercent = 100f * numberOfActiveTasks / tasks.size,
        completedTasksPercent = 100f * (totalTasks - numberOfActiveTasks) / tasks.size
    )
}

data class StatsResult(val activeTasksPercent: Float, val completedTasksPercent: Float)
