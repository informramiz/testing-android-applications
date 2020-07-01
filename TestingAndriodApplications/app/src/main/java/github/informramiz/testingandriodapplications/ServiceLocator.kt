package github.informramiz.testingandriodapplications

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import github.informramiz.testingandriodapplications.data.source.DefaultTasksRepository
import github.informramiz.testingandriodapplications.data.source.TasksRepository
import github.informramiz.testingandriodapplications.data.source.local.TasksLocalDataSource
import github.informramiz.testingandriodapplications.data.source.local.ToDoDatabase
import github.informramiz.testingandriodapplications.data.source.remote.TasksRemoteDataSource
import kotlinx.coroutines.runBlocking


/**
 * Created by Ramiz Raja on 01/07/2020.
 */
object ServiceLocator {
    private val lock = Any()
    private var database: ToDoDatabase? = null

    @Volatile
    var tasksRepository: TasksRepository? = null
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) set

    fun provideTasksRepository(context: Context): TasksRepository {
        synchronized(this) {
            return tasksRepository ?: createTasksRepository(context)
        }
    }

    private fun createTasksRepository(context: Context): TasksRepository {
        val result = DefaultTasksRepository(TasksRemoteDataSource, createLocalDataSource(context))
        tasksRepository = result
        return result
    }

    private fun createLocalDataSource(context: Context): TasksLocalDataSource {
        val database = database ?: createDatabase(context)
        return TasksLocalDataSource(database.taskDao())
    }

    private fun createDatabase(context: Context): ToDoDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            ToDoDatabase::class.java,
            "Tasks.db"
        ).build()
        database = result
        return result
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking { TasksRemoteDataSource.deleteAllTasks() }
        }
        database?.apply {
            clearAllTables()
            close()
        }
        database = null
        tasksRepository = null
    }
}