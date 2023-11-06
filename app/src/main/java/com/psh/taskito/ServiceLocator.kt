package com.psh.taskito

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.psh.taskito.data.source.Repository
import com.psh.taskito.data.source.TasksDataSource
import com.psh.taskito.data.source.TasksRepository
import com.psh.taskito.data.source.local.TaskitoDatabase
import com.psh.taskito.data.source.local.TasksLocalDataSource
import com.psh.taskito.data.source.remote.TasksRemoteDataSource
import kotlinx.coroutines.runBlocking

object ServiceLocator {
    private val lock = Any()
    private var database: TaskitoDatabase? = null

    @Volatile
    var tasksRepository: Repository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): Repository {
        synchronized(this) {
            return tasksRepository ?: createTasksRepository(context)
        }
    }

    private fun createTasksRepository(context: Context): Repository {
        val newRepo = TasksRepository(TasksRemoteDataSource, createTaskLocalDataSource(context))
        tasksRepository = newRepo
        return newRepo
    }

    private fun createTaskLocalDataSource(context: Context): TasksDataSource {
        val database = database ?: createDataBase(context)
        return TasksLocalDataSource(database.tasksDao())
    }

    private fun createDataBase(context: Context): TaskitoDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext, TaskitoDatabase::class.java, "Tasks.db"
        ).build()
        database = result
        return result
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking { TasksRemoteDataSource.deleteAllTasks() } // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            tasksRepository = null
        }
    }
}