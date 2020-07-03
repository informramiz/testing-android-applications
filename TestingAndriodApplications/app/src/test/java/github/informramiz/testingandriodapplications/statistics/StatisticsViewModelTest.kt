package github.informramiz.testingandriodapplications.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import github.informramiz.testingandriodapplications.data.Task
import github.informramiz.testingandriodapplications.data.source.FakeTasksRepository
import github.informramiz.testingandriodapplications.util.MainCoroutineRule
import github.informramiz.testingandriodapplications.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by Ramiz Raja on 03/07/2020.
 */
@ExperimentalCoroutinesApi
class StatisticsViewModelTest {
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    val instanceTaskExecutorRule = InstantTaskExecutorRule()

    // set TestingCoroutineDispatcher as Dispatcher.Main
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeTasksRepository: FakeTasksRepository

    // GIVEN: subject under test
    private lateinit var statisticsViewModel: StatisticsViewModel
    private val activeTask = Task("title1", "description1")
    private val completedTask = Task("title2", "description2", isCompleted = true)

    @Before
    fun setupViewModel() {
        fakeTasksRepository = FakeTasksRepository()
        statisticsViewModel = StatisticsViewModel(fakeTasksRepository)
    }

    @Test
    fun getStats_zeroActiveZeroComplete() {
        //WHEN: There are no tasks

        //THEN: active tasks = 0, completed task = 0
        val activeTasks = statisticsViewModel.activeTasksPercent.getOrAwaitValue()
        val completedTasks = statisticsViewModel.completedTasksPercent.getOrAwaitValue()
        assertThat(activeTasks, `is`(0f))
        assertThat(completedTasks, `is`(0f))
    }

    @Test
    fun getStats_hundredActiveZeroComplete() {
        //WHEN: There are are 100% active tasks, 0% completed tasks
        fakeTasksRepository.addTasks(activeTask)

        //THEN: active tasks = 100, completed task = 0
        val activeTasks = statisticsViewModel.activeTasksPercent.getOrAwaitValue()
        val completedTasks = statisticsViewModel.completedTasksPercent.getOrAwaitValue()
        assertThat(activeTasks, `is`(100f))
        assertThat(completedTasks, `is`(0f))
    }

    @Test
    fun getStats_zeroActiveHundredComplete() {
        //WHEN: There are are 0% active tasks, 100% completed tasks
        fakeTasksRepository.addTasks(completedTask)

        //THEN: active tasks = 0, completed task = 100
        val activeTasks = statisticsViewModel.activeTasksPercent.getOrAwaitValue()
        val completedTasks = statisticsViewModel.completedTasksPercent.getOrAwaitValue()
        assertThat(activeTasks, `is`(0f))
        assertThat(completedTasks, `is`(100f))
    }

    @Test
    fun getStats_fiftyActiveFiftyComplete() {
        //WHEN: There are are 100% active tasks, 0% completed tasks
        fakeTasksRepository.addTasks(activeTask)
        fakeTasksRepository.addTasks(completedTask)

        //THEN: active tasks = 50, completed task = 50
        val activeTasks = statisticsViewModel.activeTasksPercent.getOrAwaitValue()
        val completedTasks = statisticsViewModel.completedTasksPercent.getOrAwaitValue()
        assertThat(activeTasks, `is`(50f))
        assertThat(completedTasks, `is`(50f))
    }
}
