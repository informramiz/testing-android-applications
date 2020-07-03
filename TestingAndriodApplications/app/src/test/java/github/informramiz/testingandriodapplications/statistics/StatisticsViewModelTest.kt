package github.informramiz.testingandriodapplications.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import github.informramiz.testingandriodapplications.data.source.FakeTasksRepository
import github.informramiz.testingandriodapplications.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

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

    @Before
    fun setupViewModel() {
        fakeTasksRepository = FakeTasksRepository()
        statisticsViewModel = StatisticsViewModel(fakeTasksRepository)
    }
}
