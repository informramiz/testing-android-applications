package github.informramiz.testingandriodapplications.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import github.informramiz.testingandriodapplications.data.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
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
@SmallTest
class TasksDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    //GIVEN: subject under test
    private lateinit var database: ToDoDatabase

    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ToDoDatabase::class.java
        ).build()
    }

    @After
    fun closeDatabase() = database.close()

    @Test
    fun insertTaskAndGetById() = runBlockingTest {
        //WHEN: you insert and task and get the same task by id
        val task = Task("Title1", "description1")
        database.taskDao().insertTask(task)

        val loadedTask = database.taskDao().getTaskById(task.id)

        //THEN: the task should be returned correctly with the right values
        assertThat(loadedTask as Task, not(nullValue()))
        assertThat(loadedTask.id, `is`(task.id))
        assertThat(loadedTask, IsEqual(loadedTask))
    }
}