package com.psh.taskito.data.source.local

import androidx.room.Database
import com.psh.taskito.data.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskitoDatabase {
    abstract fun tasksDao() : TasksDao
}