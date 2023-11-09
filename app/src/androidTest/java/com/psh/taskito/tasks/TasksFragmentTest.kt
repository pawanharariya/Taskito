package com.psh.taskito.tasks

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.psh.taskito.R
import com.psh.taskito.ServiceLocator
import com.psh.taskito.data.Task
import com.psh.taskito.data.source.Repository
import com.psh.taskito.data.source.FakeAndroidTestRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class TasksFragmentTest {
    private lateinit var repository: Repository

    @Before
    fun initRepository() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun cleanupDb() = runTest {
        ServiceLocator.resetRepository()
    }

    @Test
    fun clickTask_navigateToDetailFragment() = runTest {
        repository.saveTask(Task("TITLE1", "DESCRIPTION1", false, "id1"))
        repository.saveTask(Task("TITLE2", "DESCRIPTION2", true, "id2"))

        // Create a TestNavHostController
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        // Create a graphical FragmentScenario
        val scenario =
            launchFragmentInContainer<TasksFragment>(Bundle(), R.style.Base_Theme_Taskito)

        scenario.onFragment { fragment ->
            // Set the graph on the TestNavHostController
            navController.setGraph(R.navigation.nav_graph)

            // Make the NavController available via the findNavController() APIs
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        // perform recycler view item click using Espresso
        onView(withId(R.id.tasks_list)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                hasDescendant(withText("TITLE1")), click()
            )
        )

        // assertion that navController has correct fragment destination
        assertEquals(navController.currentDestination?.id, R.id.fragment_task_detail)
    }

    @Test
    fun clickAddTaskButton_navigateToAddEditTaskFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val scenario =
            launchFragmentInContainer<TasksFragment>(Bundle(), R.style.Base_Theme_Taskito)
        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
        onView(withId(R.id.add_task_fab)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.fragment_add_edit_task)
    }
}