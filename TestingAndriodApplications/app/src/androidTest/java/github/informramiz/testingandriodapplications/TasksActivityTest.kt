package github.informramiz.testingandriodapplications

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import github.informramiz.testingandriodapplications.data.Task
import github.informramiz.testingandriodapplications.data.source.TasksRepository
import github.informramiz.testingandriodapplications.tasks.TasksActivity
import github.informramiz.testingandriodapplications.util.DataBindingIdlingResource
import github.informramiz.testingandriodapplications.util.EspressoIdlingResource
import github.informramiz.testingandriodapplications.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by Ramiz Raja on 03/07/2020.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class TasksActivityTest {
    private lateinit var tasksRepository: TasksRepository

    //idling resource to signal when Espresso has to wait for data binding to complete its bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun setup() {
        tasksRepository =
            ServiceLocator.provideTasksRepository(ApplicationProvider.getApplicationContext())
        runBlocking { tasksRepository.deleteAllTasks() }
    }

    @After
    fun teardown() {
        ServiceLocator.resetRepository()
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResources() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResources() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun editTask_showsEditTaskCorrectly() = runBlocking {
        //GIVEN: a saved task in list
        val task = Task("Title1", "Description")
        tasksRepository.saveTask(task)

        //launch activity
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        //let the DataBindingIdlingResource monitor the activity for any pending data bindings
        //to signal Espresso accordingly
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //1. click on a task in the list
        onView(withText(task.title)).perform(click())
        //2. verify that detail screen is correct
        onView(withId(R.id.task_detail_title_text))
            .check(matches(withText(task.title)))
        onView(withId(R.id.task_detail_description_text))
            .check(matches(withText(task.description)))
        onView(withId(R.id.task_detail_complete_checkbox))
            .check(matches(isNotChecked()))

        //WHEN: Task is edited and saved
        val editedTask = task.copy("Title2", "Description2")
        onView(withId(R.id.edit_task_fab))
            .perform(click())
        onView(withId(R.id.add_task_title_edit_text))
            .perform(replaceText(editedTask.title))
        onView(withId(R.id.add_task_description_edit_text))
            .perform(replaceText(task.description))
        onView(withId(R.id.save_task_fab)).perform(click())

        //THEN: It should be displayed correctly in list and old task should be removed
        onView(withText(editedTask.title))
            .check(matches(isDisplayed()))
        onView(withText(task.title)).check(doesNotExist())

        // Make sure the activity is closed before resetting the db.
        activityScenario.close()
    }
}