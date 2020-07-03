package github.informramiz.testingandriodapplications.statistics

import androidx.lifecycle.*
import github.informramiz.testingandriodapplications.data.Result
import github.informramiz.testingandriodapplications.data.Result.Error
import github.informramiz.testingandriodapplications.data.Result.Success
import github.informramiz.testingandriodapplications.data.Task
import github.informramiz.testingandriodapplications.data.source.TasksRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for the statistics screen.
 */
class StatisticsViewModel(private val tasksRepository: TasksRepository) : ViewModel() {
    private val tasks: LiveData<Result<List<Task>>> = tasksRepository.observeTasks()
    private val _dataLoading = MutableLiveData<Boolean>(false)
    private val stats: LiveData<StatsResult?> = tasks.map {
        if (it is Success) {
            getActiveAndCompletedStats(it.data)
        } else {
            null
        }
    }

    val activeTasksPercent = stats.map {
        it?.activeTasksPercent ?: 0f }
    val completedTasksPercent: LiveData<Float> = stats.map { it?.completedTasksPercent ?: 0f }
    val dataLoading: LiveData<Boolean> = _dataLoading
    val error: LiveData<Boolean> = tasks.map { it is Error }
    val empty: LiveData<Boolean> = tasks.map { (it as? Success)?.data.isNullOrEmpty() }

    fun refresh() {
        _dataLoading.value = true
            viewModelScope.launch {
                tasksRepository.refreshTasks()
                _dataLoading.value = false
            }
    }

    class StatisticsViewModelFactory(private val tasksRepository: TasksRepository) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return StatisticsViewModel(tasksRepository) as T
        }
    }
}
