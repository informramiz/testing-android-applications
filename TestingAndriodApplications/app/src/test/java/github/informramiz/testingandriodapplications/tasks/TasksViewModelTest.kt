package github.informramiz.testingandriodapplications.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import github.informramiz.testingandriodapplications.data.Task
import github.informramiz.testingandriodapplications.data.source.FakeTasksRepository
import github.informramiz.testingandriodapplications.util.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Before
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

    //dependencies
    private lateinit var fakeTasksRepository: FakeTasksRepository

    //subject under test
    private lateinit var tasksViewModel: TasksViewModel

    //make sure each test has a fresh instance of subject under test
    @Before
    fun setup() {
        fakeTasksRepository = FakeTasksRepository()
        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2", true)
        val task3 = Task("Title3", "Description3", true)
        fakeTasksRepository.addTasks(task1, task2, task3)

        tasksViewModel = TasksViewModel(fakeTasksRepository)
    }

    @Test
    fun addNewTask_setsNewTaskEvent() {
        //WHEN:
        tasksViewModel.addNewTask()

        //THEN:
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()
        assertThat(value.getContentIfNotHandled(), not(nullValue()))
    }

    @Test
    fun setFilterAllTasks_addTaskButtonVisible() {
        //WHEN
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)

        //THEN
        val value = tasksViewModel.tasksAddViewVisible.getOrAwaitValue()
        assertThat(value, `is`(true))
    }
}