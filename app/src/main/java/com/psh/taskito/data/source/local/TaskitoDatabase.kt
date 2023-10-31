package com.psh.taskito.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.psh.taskito.data.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskitoDatabase : RoomDatabase() {
    abstract fun tasksDao() : TasksDao
}