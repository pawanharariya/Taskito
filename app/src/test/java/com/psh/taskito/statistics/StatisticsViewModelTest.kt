package com.psh.taskito.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.psh.taskito.MainDispatcherRule
import com.psh.taskito.data.source.FakeTestRepository
import com.psh.taskito.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class StatisticsViewModelTest {

    // Replaces Main dispatcher with a TestDispatcher for local unit tests
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Fake repository to be injected into ViewModel
    private lateinit var repository: FakeTestRepository

    // Subject under test
    private lateinit var statisticsViewModel: StatisticsViewModel

    @Before
    fun setUp() {
        repository = FakeTestRepository()
        statisticsViewModel = StatisticsViewModel(repository)
    }

    @Test
    fun loadTasks_loading() {
        // TODO migrate StateFlow and test UI state
        statisticsViewModel.refresh()
        assertFalse(statisticsViewModel.dataLoading.getOrAwaitValue())
    }

    @Test
    fun `load statistics when tasks are unavailable call error to display`() {

        repository.setReturnError(true)
        statisticsViewModel.refresh()
        assertTrue(statisticsViewModel.empty.getOrAwaitValue())
        assertTrue(statisticsViewModel.error.getOrAwaitValue())
    }
}