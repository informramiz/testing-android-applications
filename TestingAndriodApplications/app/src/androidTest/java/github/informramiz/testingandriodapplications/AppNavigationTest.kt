package github.informramiz.testingandriodapplications

import android.app.Activity
import android.view.Gravity
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import github.informramiz.testingandriodapplications.data.source.TasksRepository
import github.informramiz.testingandriodapplications.tasks.TasksActivity
import github.informramiz.testingandriodapplications.util.DataBindingIdlingResource
import github.informramiz.testingandriodapplications.util.EspressoIdlingResource
import github.informramiz.testingandriodapplications.util.monitorActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by Ramiz Raja on 05/07/2020.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class AppNavigationTest {
    private lateinit var repository: TasksRepository
    //Idling resource to signal Espresso to wait until data binding finishes
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun setup() {
        repository = ServiceLocator.provideTasksRepository(ApplicationProvider.getApplicationContext())
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun teardown() {
        ServiceLocator.resetRepository()
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun tasksScreen_clickOnDrawer_OpensNavigation() {
        //GIVEN: start the tasks screen and register the databinding listening resource for Espresso
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //verify that the drawer is open on launch
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START)))

        //WHEN: you click on Up/Hamburger button
        onView(withContentDescription(activityScenario.getToolbarNavigationButtonContentDescription()))
            .perform(click())

        //THEN: drawer should open
        onView(withId(R.id.drawer_layout))
            .check(matches(isOpen(Gravity.START)))

        //close activity scenario
        activityScenario.close()
    }
}

/**
 * Returns the content description for the Up/Hamburger button in the toolbar
 */
fun <T: Activity> ActivityScenario<T>.getToolbarNavigationButtonContentDescription(): String {
    var description = ""
    onActivity {
        description = it.findViewById<Toolbar>(R.id.toolbar).navigationContentDescription as String
    }

    return description
}