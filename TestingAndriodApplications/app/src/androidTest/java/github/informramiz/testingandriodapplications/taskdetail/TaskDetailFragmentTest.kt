package github.informramiz.testingandriodapplications.taskdetail

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import github.informramiz.testingandriodapplications.R
import github.informramiz.testingandriodapplications.ServiceLocator
import github.informramiz.testingandriodapplications.data.Task
import github.informramiz.testingandriodapplications.data.source.FakeTasksRepository
import github.informramiz.testingandriodapplications.data.source.TasksRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Ramiz Raja on 01/07/2020.
 */
@ExperimentalCoroutinesApi
@MediumTest  //allows to group small, medium and large tests and run them in groups
@RunWith(AndroidJUnit4::class)
class TaskDetailFragmentTest {
    private lateinit var repository: TasksRepository

    @Before
    fun setup() {
        repository = FakeTasksRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun teardown() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

    @Test
    fun activeTaskDetails_displayInUI() = runBlockingTest {
        //GIVEN: active task
        val activeTask = Task("My Task", "My task is active", isCompleted = false)
        repository.saveTask(activeTask)

        //WHEN: TaskDetailFragment is launched
        val args = TaskDetailFragmentArgs(activeTask.id).toBundle()
        launchFragmentInContainer<TaskDetailFragment>(args, R.style.AppTheme)

        //THEN: it should display the details of the task correctly

        //1. Make sure title is displayed correctly
        onView(withId(R.id.task_detail_title_text))
            .check(matches(isDisplayed()))
            .check(matches(withText(activeTask.title)))

        //2. Make sure description is displayed correctly
        onView(withId(R.id.task_detail_description_text))
            .check(matches(isDisplayed()))
            .check(matches(withText(activeTask.description)))

        //3. Make sure checkbox is there and it is not checked
        onView(withId(R.id.task_detail_complete_checkbox))
            .check(matches(isDisplayed()))
            .check(matches(not(isChecked())))
    }
}