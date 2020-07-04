package github.informramiz.testingandriodapplications.data.source

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Room
import github.informramiz.testingandriodapplications.data.Result
import github.informramiz.testingandriodapplications.data.Result.Success
import github.informramiz.testingandriodapplications.data.Task
import github.informramiz.testingandriodapplications.data.source.local.TasksLocalDataSource
import github.informramiz.testingandriodapplications.data.source.local.ToDoDatabase
import github.informramiz.testingandriodapplications.data.source.remote.TasksRemoteDataSource
import github.informramiz.testingandriodapplications.util.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 */
class DefaultTasksRepository constructor(
    private val tasksRemoteDataSource: TasksDataSource,
    private val tasksLocalDataSource: TasksDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : TasksRepository {

    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        wrapEspressoIdlingResource {
            if (forceUpdate) {
                try {
                    updateTasksFromRemoteDataSource()
                } catch (ex: Exception) {
                    return Result.Error(ex)
                }
            }
            return tasksLocalDataSource.getTasks()
        }
    }

    override suspend fun refreshTasks() {
        wrapEspressoIdlingResource {
            updateTasksFromRemoteDataSource()
        }
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return tasksLocalDataSource.observeTasks()
    }

    override suspend fun refreshTask(taskId: String) {
        wrapEspressoIdlingResource {
            updateTaskFromRemoteDataSource(taskId)
        }
    }

    private suspend fun updateTasksFromRemoteDataSource() {
        wrapEspressoIdlingResource {
            val remoteTasks = tasksRemoteDataSource.getTasks()

            if (remoteTasks is Success) {
                // Real apps might want to do a proper sync.
                tasksLocalDataSource.deleteAllTasks()
                remoteTasks.data.forEach { task ->
                    tasksLocalDataSource.saveTask(task)
                }
            } else if (remoteTasks is Result.Error) {
                throw remoteTasks.exception
            }
        }
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        wrapEspressoIdlingResource {
            return tasksLocalDataSource.observeTask(taskId)
        }
    }

    private suspend fun updateTaskFromRemoteDataSource(taskId: String) {
        wrapEspressoIdlingResource {
            val remoteTask = tasksRemoteDataSource.getTask(taskId)

            if (remoteTask is Success) {
                tasksLocalDataSource.saveTask(remoteTask.data)
            }
        }
    }

    /**
     * Relies on [getTasks] to fetch data and picks the task with the same ID.
     */
    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
        wrapEspressoIdlingResource {
            if (forceUpdate) {
                updateTaskFromRemoteDataSource(taskId)
            }
            return tasksLocalDataSource.getTask(taskId)
        }
    }

    override suspend fun saveTask(task: Task) {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { tasksRemoteDataSource.saveTask(task) }
                launch { tasksLocalDataSource.saveTask(task) }
            }
        }
    }

    override suspend fun completeTask(task: Task) {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { tasksRemoteDataSource.completeTask(task) }
                launch { tasksLocalDataSource.completeTask(task) }
            }
        }
    }

    override suspend fun completeTask(taskId: String) {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                (getTaskWithId(taskId) as? Success)?.let { it ->
                    completeTask(it.data)
                }
            }
        }
    }

    override suspend fun activateTask(task: Task) = withContext<Unit>(ioDispatcher) {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { tasksRemoteDataSource.activateTask(task) }
                launch { tasksLocalDataSource.activateTask(task) }
            }
        }
    }

    override suspend fun activateTask(taskId: String) {
        withContext(ioDispatcher) {
            (getTaskWithId(taskId) as? Success)?.let { it ->
                activateTask(it.data)
            }
        }
    }

    override suspend fun clearCompletedTasks() {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { tasksRemoteDataSource.clearCompletedTasks() }
                launch { tasksLocalDataSource.clearCompletedTasks() }
            }
        }
    }

    override suspend fun deleteAllTasks() {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                coroutineScope {
                    launch { tasksRemoteDataSource.deleteAllTasks() }
                    launch { tasksLocalDataSource.deleteAllTasks() }
                }
            }
        }
    }

    override suspend fun deleteTask(taskId: String) {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { tasksRemoteDataSource.deleteTask(taskId) }
                launch { tasksLocalDataSource.deleteTask(taskId) }
            }
        }
    }

    private suspend fun getTaskWithId(id: String): Result<Task> {
        wrapEspressoIdlingResource {
            return tasksLocalDataSource.getTask(id)
        }
    }
}
