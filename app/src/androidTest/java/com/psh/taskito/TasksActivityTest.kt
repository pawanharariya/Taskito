package com.psh.taskito

import androidx.test.core.app.ActivityScenario.*
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.psh.taskito.data.Task
import com.psh.taskito.data.source.Repository
import com.psh.taskito.tasks.TasksActivity
import com.psh.taskito.util.DataBindingIdlingResource
import com.psh.taskito.util.EspressoIdlingResource
import com.psh.taskito.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TasksActivityTest {
    private lateinit var repository: Repository

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        repository =
            ServiceLocator.provideTasksRepository(getApplicationContext())
        runBlocking {
            repository.deleteAllTasks()
        }
    }

    @After
    fun reset() {
        ServiceLocator.resetRepository()
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun editTask() = runBlocking {
        repository.saveTask(Task("TITLE1", "DESCRIPTION"))

        // Start up Tasks screen
        val activityScenario = launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list and verify that all the data is correct
        onView(withText("TITLE1")).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("TITLE1")))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("DESCRIPTION")))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))

        // Click on the edit button, edit, and save
        onView(withId(R.id.edit_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("NEW TITLE"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("NEW DESCRIPTION"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // Verify task is displayed on screen in the task list.
        onView(withText("NEW TITLE")).check(matches(isDisplayed()))
        // Verify previous task is not displayed
        onView(withText("TITLE1")).check(doesNotExist())

        // Make sure the activity is closed before resetting the db:
        activityScenario.close()
    }

    @Test
    fun createOneTask_deleteTask() {

        // start up Tasks screen
        val activityScenario = launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Add active task
        onView(withId(R.id.add_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text))
            .perform(typeText("TITLE1"), closeSoftKeyboard())
        onView(withId(R.id.add_task_description_edit_text)).perform(typeText("DESCRIPTION"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // Open it in details view
        onView(withText("TITLE1")).perform(click())
        // Click delete task in menu
        onView(withId(R.id.menu_delete)).perform(click())

        // Verify it was deleted
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(doesNotExist())
        // Make sure the activity is closed before resetting the db
        activityScenario.close()
    }
}