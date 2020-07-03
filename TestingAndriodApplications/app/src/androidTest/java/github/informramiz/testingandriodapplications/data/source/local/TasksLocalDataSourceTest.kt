package github.informramiz.testingandriodapplications.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import github.informramiz.testingandriodapplications.data.Result
import github.informramiz.testingandriodapplications.data.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Ramiz Raja on 03/07/2020.
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class TasksLocalDataSourceTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val task = Task("Title1", "Description1")
    private lateinit var database: ToDoDatabase
    //GIVEN: subject under test
    private lateinit var localDataSource: TasksLocalDataSource

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ToDoDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        localDataSource = TasksLocalDataSource(database.taskDao(), Dispatchers.Main)
    }

    @After
    fun teardown() = database.close()

    // runBlocking is used here because of https://github.com/Kotlin/kotlinx.coroutines/issues/1204
    // TODO: Replace with runBlockingTest once issue is resolved
    @Test
    fun saveTaskAndGetTask_returnsValidTask() = runBlocking {
        //WHEN: A task is saved and then retrieved with same id
        localDataSource.saveTask(task)
        val result = localDataSource.getTask(task.id)

        //THEN: the loaded task should be same as saved one
        assertThat(result is Result.Success, `is`(true))
        assertThat((result as Result.Success).data, IsEqual(task))
    }

    // runBlocking is used here because of https://github.com/Kotlin/kotlinx.coroutines/issues/1204
    // TODO: Replace with runBlockingTest once issue is resolved
    @Test
    fun completeTask_returnsTaskAsComplete() = runBlocking {
        //WHEN:
        //1. Save task
        localDataSource.saveTask(task)
        //2. Mark task as complete
        localDataSource.completeTask(task)
        //3. load the task
        val result = localDataSource.getTask(task.id)

        //THEN: the loaded task should be marked as complete
        assertThat(result is Result.Success, `is`(true))
        assertThat((result as Result.Success).data.isCompleted, `is`(true))
    }
}