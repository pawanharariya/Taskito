package com.psh.taskito.data.source

import com.psh.taskito.data.Result.Success
import com.psh.taskito.data.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TasksRepositoryTest {
    private val task1 = Task("Title1", "Description1")
    private val task2 = Task("Title2", "Description2")
    private val task3 = Task("Title3", "Description3")
    private val remoteTasks = listOf(task1, task2).sortedBy { it.id }
    private val localTasks = listOf(task3).sortedBy { it.id }
    private val newTasks = listOf(task3).sortedBy { it.id }

    private lateinit var localDataSource: FakeDataSource
    private lateinit var remoteDataSource: FakeDataSource
    private lateinit var tasksRepository: TasksRepository

    @Before
    fun createRepository() {
        remoteDataSource = FakeDataSource(remoteTasks.toMutableList())
        localDataSource = FakeDataSource(localTasks.toMutableList())
        tasksRepository = TasksRepository(remoteDataSource, localDataSource, Dispatchers.Unconfined)
    }

    @Test
    fun getTasks_requestAllTasksFromRemoteDataSource() = runTest {
        val tasks = tasksRepository.getTasks(true) as Success
        assertEquals(remoteTasks, tasks.data)
    }
}