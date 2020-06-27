package github.informramiz.testingandriodapplications.data

import androidx.lifecycle.LiveData
import github.informramiz.testingandriodapplications.data.source.TasksDataSource


/**
 * Created by Ramiz Raja on 28/06/2020.
 */
class FakeTasksDataSource(var tasks: MutableList<Task>? = mutableListOf()) : TasksDataSource {

    override suspend fun saveTask(task: Task) {
        tasks?.add(task)
    }

    override suspend fun deleteAllTasks() {
        tasks?.clear()
    }

    override suspend fun getTasks(): Result<List<Task>> {
        return tasks?.let { Result.Success(it) } ?: Result.Error(Exception("No Tasks found"))
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshTasks() {
        TODO("Not yet implemented")
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun completeTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun completeTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun activateTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun activateTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clearCompletedTasks() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(taskId: String) {
        TODO("Not yet implemented")
    }
}