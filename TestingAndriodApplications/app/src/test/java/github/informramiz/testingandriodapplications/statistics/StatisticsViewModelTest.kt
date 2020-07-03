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

    @Test
    fun refreshTasks_loading() {
        //we want to test that loading status changes in following ways
        //1. Loading = true, when data is loading
        // 2. Loading = false, when data is loaded.
        //so we want to check the asynchronous logic here so we are not going to use runBlockTest
        //instead we are going to use pause/resume for dispatcher

        //as soon as a coroutine tries to launch, pause it immediately before executing the coroutine
        mainCoroutineRule.pauseDispatcher()
        //WHEN: Tasks are refreshed
        statisticsViewModel.refresh() //this will be paused at viewModelScope.launch {} line

        //THEN:
        //1. When data is loading, loading status should be true
        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(true))
        //now resume the dispatcher so that data is loaded
        mainCoroutineRule.resumeDispatcher()
        //2. When data is loaded, loading status should be false
        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun refreshTasks_whenTasksNotAvailable_displayError() {
        //WHEN: tasks are not available for no internet or any other reason
        fakeTasksRepository.setReturnError(true)
        statisticsViewModel.refresh()

        //THEN: error should be set and tasks empty status should set
        assertThat(statisticsViewModel.error.getOrAwaitValue(), `is`(true))
        assertThat(statisticsViewModel.empty.getOrAwaitValue(), `is`(true))

        //reset the error to false
        fakeTasksRepository.setReturnError(false)
    }
}
