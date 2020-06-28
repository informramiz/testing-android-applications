package github.informramiz.testingandriodapplications.data.source

import github.informramiz.testingandriodapplications.data.Result
import github.informramiz.testingandriodapplications.data.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsEqual
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Created by Ramiz Raja on 28/06/2020.
 */
@ExperimentalCoroutinesApi
class DefaultTasksRepositoryTest {
    private val task1 = Task("Title1", "Description1")
    private val task2 = Task("Title2", "Description2")
    private val task3 = Task("Title3", "Description3")

    private val remoteTasks = listOf(task1, task2).sortedBy { it.id }
    private val localTasks = listOf(task3).sortedBy { it.id }
    private val newTasks = listOf(task3).sortedBy { it.id }

    //dependencies
    private lateinit var remoteDataSource: TasksDataSource
    private lateinit var localDataSource: TasksDataSource

    //GIVEN: Subject under test
    private lateinit var defaultTasksRepository: DefaultTasksRepository

    @Before
    fun setup() {
        remoteDataSource =
            FakeTasksDataSource(
                remoteTasks.toMutableList()
            )
        localDataSource =
            FakeTasksDataSource(
                localTasks.toMutableList()
            )
        defaultTasksRepository = DefaultTasksRepository(remoteDataSource, localDataSource, Dispatchers.Unconfined)
    }

    @Test
    fun getTasks_requestAllTasksFromRemote_returnsOnlyRemoteTasks() = runBlockingTest {
        //WHEN: get tasks using force update
        val tasks = defaultTasksRepository.getTasks(true) as Result.Success
        //THEN: should return remoteTasks
        assertThat(tasks.data, IsEqual(remoteTasks))
    }
}