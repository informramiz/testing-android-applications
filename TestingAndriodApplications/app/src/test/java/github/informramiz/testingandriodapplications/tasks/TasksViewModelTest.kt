package github.informramiz.testingandriodapplications.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import github.informramiz.testingandriodapplications.util.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Ramiz Raja on 27/06/2020.
 */
@RunWith(AndroidJUnit4::class)
class TasksViewModelTest {
    //let's make sure all background operations run on the same thread to keep them
    //in order to avoid any undesired behavior
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @Test
    fun addNewTask_setsNewTaskEvent() {
        //GIVEN:
        val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())

        //WHEN:
        tasksViewModel.addNewTask()

        //THEN:
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()
        assertThat(value.getContentIfNotHandled(), not(nullValue()))
    }

    @Test
    fun setFilterAllTasks_addTaskButtonVisible() {
        //GIVEN
        val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())

        //WHEN
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)

        //THEN
        val value = tasksViewModel.tasksAddViewVisible.getOrAwaitValue()
        assertThat(value, `is`(true))
    }
}