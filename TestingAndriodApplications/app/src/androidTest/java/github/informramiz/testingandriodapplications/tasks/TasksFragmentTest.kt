package github.informramiz.testingandriodapplications.tasks

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
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
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

/**
 * Created by Ramiz Raja on 01/07/2020.
 */
@MediumTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class TasksFragmentTest {
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
    fun clickTask_navigateToDetailFragmentOne() = runBlockingTest {
        //GIVEN: active task
        val activeTask1 = Task("My Task1", "My task is active", isCompleted = false)
        val activeTask2 = Task("My Task2", "My task is active", isCompleted = true)
        repository.saveTask(activeTask1)
        repository.saveTask(activeTask2)

        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        val navControllerMock = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.requireView(), navControllerMock)
        }

        //WHEN: A task item is clicked in the list
        onView(withId(R.id.tasks_list))
            .perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(hasDescendant(
                withText(activeTask1.title)), click()))

        //THEN: app should navigate to the detail fragment
        verify(navControllerMock).navigate(TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment(activeTask1.id))
    }
}