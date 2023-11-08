package com.psh.taskito.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.psh.taskito.Event
import com.psh.taskito.MainDispatcherRule
import com.psh.taskito.R
import com.psh.taskito.data.Task
import com.psh.taskito.data.source.FakeTestRepository
import com.psh.taskito.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TasksViewModelTest {

    private lateinit var tasksViewModel: TasksViewModel
    private lateinit var fakeTestRepository: FakeTestRepository

    // Replaces Main dispatcher with a TestDispatcher for local unit tests
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        fakeTestRepository = FakeTestRepository()
        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2", true)
        val task3 = Task("Title3", "Description3", true)
        fakeTestRepository.addTasks(task1, task2, task3)
        tasksViewModel = TasksViewModel(fakeTestRepository)
    }

    @Test
    fun addNewTask_setsNewTaskEvent() {
        tasksViewModel.addNewTask()
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()
        assertNotNull(value.getContentIfNotHandled())
    }

    @Test
    fun setFilterAllTasks_tasksAddViewVisible() {
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)
        val value = tasksViewModel.tasksAddViewVisible.getOrAwaitValue()
        assertTrue(value)
    }

    @Test
    fun completeTask_dataAndSnackbarUpdated() {
        val task = Task("Task", "Description")
        fakeTestRepository.addTasks(task)
        tasksViewModel.completeTask(task, true)

        fakeTestRepository.tasksServiceData[task.id]?.isCompleted?.let { assertTrue(it) }

        val snackBarText: Event<Int> = tasksViewModel.snackbarText.getOrAwaitValue()
        assertEquals(R.string.task_marked_complete, snackBarText.getContentIfNotHandled())
    }

}