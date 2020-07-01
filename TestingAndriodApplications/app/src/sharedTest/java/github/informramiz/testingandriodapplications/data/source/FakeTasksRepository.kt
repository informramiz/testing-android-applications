package github.informramiz.testingandriodapplications.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import github.informramiz.testingandriodapplications.data.Result
import github.informramiz.testingandriodapplications.data.Task
import kotlinx.coroutines.runBlocking


/**
 * Created by Ramiz Raja on 28/06/2020.
 */
class FakeTasksRepository : TasksRepository {
    //Map to keep track of tasks state
    var tasksServiceData = LinkedHashMap<String, Task>()
    //A LiveData to return from methods
    private val observableTasks = MutableLiveData<Result<List<Task>>>()

    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        return Result.Success(tasksServiceData.values.toList())
    }

    override suspend fun refreshTasks() {
        observableTasks.value = getTasks()
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        runBlocking { refreshTasks() }
        return observableTasks
    }

    override suspend fun saveTask(task: Task) {
        tasksServiceData[task.id] = task
    }

    override suspend fun refreshTask(taskId: String) {
       refreshTasks()
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        runBlocking { refreshTasks() }
        return observableTasks.map { tasksResult ->
            when(tasksResult) {
                is Result.Loading -> Result.Loading
                is Result.Error -> Result.Error(tasksResult.exception)
                is Result.Success -> {
                    val result = tasksResult.data.firstOrNull() { it.id == taskId }
                    result ?: return@map Result.Error(Exception("Not task found"))
                    Result.Success(result)
                }
            }
        }
    }

    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
        return tasksServiceData[taskId]?.let { Result.Success(it) } ?: Result.Error(Exception("No task found"))
    }

    override suspend fun completeTask(task: Task) {
        completeTask(task.id)
    }

    override suspend fun completeTask(taskId: String) {
        setTaskStatus(taskId, true)
    }

    override suspend fun activateTask(task: Task) {
        activateTask(task.id)
    }

    override suspend fun activateTask(taskId: String) {
        setTaskStatus(taskId, isComplete = false)
    }

    private suspend fun setTaskStatus(taskId: String, isComplete: Boolean) {
        val task = tasksServiceData[taskId] ?: return
        tasksServiceData[taskId] = task.copy(isCompleted = isComplete)
        refreshTasks()
    }

    override suspend fun clearCompletedTasks() {
        tasksServiceData = tasksServiceData.filter { !it.value.isCompleted } as LinkedHashMap<String, Task>
    }

    override suspend fun deleteAllTasks() {
        tasksServiceData.clear()
        refreshTasks()
    }

    override suspend fun deleteTask(taskId: String) {
        tasksServiceData.remove(taskId)
        refreshTasks()
    }

    //helper method to add tasks
    fun addTasks(vararg tasks: Task) {
        for (task in tasks) {
            tasksServiceData[task.id] = task
        }
        runBlocking { refreshTasks() }
    }
}