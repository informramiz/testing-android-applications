package github.informramiz.testingandriodapplications.data.source

import androidx.lifecycle.LiveData
import github.informramiz.testingandriodapplications.data.Result
import github.informramiz.testingandriodapplications.data.Task


/**
 * Created by Ramiz Raja on 28/06/2020.
 */
interface TasksRepository {
    suspend fun getTasks(forceUpdate: Boolean = false): Result<List<Task>>

    suspend fun refreshTasks()
    fun observeTasks(): LiveData<Result<List<Task>>>

    suspend fun refreshTask(taskId: String)
    fun observeTask(taskId: String): LiveData<Result<Task>>

    /**
     * Relies on [getTasks] to fetch data and picks the task with the same ID.
     */
    suspend fun getTask(taskId: String, forceUpdate: Boolean = false): Result<Task>

    suspend fun saveTask(task: Task)

    suspend fun completeTask(task: Task)

    suspend fun completeTask(taskId: String)

    suspend fun activateTask(task: Task)

    suspend fun activateTask(taskId: String)

    suspend fun clearCompletedTasks()

    suspend fun deleteAllTasks()

    suspend fun deleteTask(taskId: String)
}