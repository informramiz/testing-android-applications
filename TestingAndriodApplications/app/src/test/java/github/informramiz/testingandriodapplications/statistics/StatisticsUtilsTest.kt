package github.informramiz.testingandriodapplications.statistics

import github.informramiz.testingandriodapplications.data.Task
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test


/**
 * Created by Ramiz Raja on 27/06/2020.
 */
class StatisticsUtilsTest {
    //if there is one active task and no completed task then there are 100%
    //active tasks and 0% completed tasks
    @Test
    fun getActiveAndCompletedStats_noCompleted_returnsZeroHundred() {
        //GIVEN: list of tasks containing only one active task
        val tasks = listOf(Task("title", "description", isCompleted = false))

        //WHEN: you call getActiveAndCompleteStats
        val result = getActiveAndCompletedStats(tasks)

        //THEN: there should be 100% active and 0% completed tasks
        assertThat(result.activeTasksPercent, `is`(100f))
        assertThat(result.completedTasksPercent, `is`(0f))
    }
}
