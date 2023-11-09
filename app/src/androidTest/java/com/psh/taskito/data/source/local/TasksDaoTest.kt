package com.psh.taskito.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.psh.taskito.data.Task
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class TasksDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TaskitoDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(), TaskitoDatabase::class.java
        ).build()
    }

    @After
    fun cleanUp() = database.close()

    @Test
    fun insertTask_getTaskById_shouldMatch() = runTest {
        val task = Task("Title", "Description")
        database.tasksDao().insertTask(task)
        val loadedTask = database.tasksDao().getTaskById(task.id)
        assertEquals(task, loadedTask)
    }

    @Test
    fun updateTask_getTaskById_shouldMatch() = runTest {
        val originalTask = Task("Title", "Description")
        database.tasksDao().insertTask(originalTask)

        val updatedTask = Task("Updated Title", "Updated Description", false, originalTask.id)
        database.tasksDao().updateTask(updatedTask)

        val loadedTask = database.tasksDao().getTaskById(originalTask.id)

        assertEquals(updatedTask, loadedTask)
    }
}