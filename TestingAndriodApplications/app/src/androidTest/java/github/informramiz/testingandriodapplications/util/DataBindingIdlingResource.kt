package github.informramiz.testingandriodapplications.util

import android.app.Activity
import android.view.View
import androidx.core.view.postDelayed
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.IdlingResource
import java.util.*


/**
 * Created by Ramiz Raja on 04/07/2020.
 */
class DataBindingIdlingResource : IdlingResource {
    //id to make this resource instances always unique
    private val id = UUID.randomUUID().toString()
    //list to keep track of register callbacks
    private val idlingCallbacks = mutableListOf<IdlingResource.ResourceCallback>()
    //keep track of last state as we only need to call onTransitionToIdle() when the resource
    //was actually busy before
    private var wasNotIdle = false

    //current activity under test
    lateinit var activity: FragmentActivity

    override fun getName(): String {
        return "DataBinding $id"
    }

    override fun isIdleNow(): Boolean {
        //a view is considered idle only when there are no pending bindings
        val isIdle = !getBindings().any { it.hasPendingBindings() }

        if (isIdle) {
            if (wasNotIdle) {
                idlingCallbacks.forEach { it.onTransitionToIdle() }
            }
            wasNotIdle = false
        } else {
            wasNotIdle = true
            //schedule a check when next time the screen refreshes (screen refresh after 16ms)
            activity.mainView.postDelayed(16) { isIdleNow }
        }

        return isIdleNow
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        idlingCallbacks.add(callback)
    }

    /* Get all the bindings in all the loaded fragments */
    private fun getBindings(): List<ViewDataBinding> {
        val topLevelFragments = activity.supportFragmentManager.fragments
        val topLevelBindings = topLevelFragments.mapNotNull { it.view?.getBinding() }

        //now find the bindings of child fragments of each top level fragment
        val childBindings = topLevelFragments.flatMap { it.childFragmentManager.fragments }.mapNotNull { it.view?.getBinding() }

        return topLevelBindings + childBindings
    }
}

private fun View.getBinding(): ViewDataBinding? = DataBindingUtil.getBinding(this)
private val Activity.mainView: View
    get() = findViewById(android.R.id.content)

//call this method to let the DataBindingIdlingResource monitor the activity
fun DataBindingIdlingResource.monitorActivity(activityScenario: ActivityScenario<out FragmentActivity>) {
    activityScenario.onActivity {
        this.activity = it
    }
}

//call this method to let the DataBindingIdlingResource monitor the activity
fun DataBindingIdlingResource.monitorFragment(fragmentScenario: FragmentScenario<out Fragment>) {
    fragmentScenario.onFragment {
        this.activity = it.requireActivity()
    }
}