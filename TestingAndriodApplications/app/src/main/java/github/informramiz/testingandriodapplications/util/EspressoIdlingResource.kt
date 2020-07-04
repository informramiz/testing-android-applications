package github.informramiz.testingandriodapplications.util

import androidx.test.espresso.idling.CountingIdlingResource


/**
 * Created by Ramiz Raja on 03/07/2020.
 */
object EspressoIdlingResource {
    private const val RESOURCE = "GLOBAL"
    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}

inline fun <T> wrapEspressoIdlingResource(block: () -> T): T {
    EspressoIdlingResource.increment()
    return try {
        block()
    } finally {
        EspressoIdlingResource.decrement()
    }
}