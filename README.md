# testing-android-applications
A showcase of how to test different parts of an Android application using automated tests.


![Edit and Save Task](demos/edit_save_task_test.gif) ![Task Details](demos/task_details_test.gif) 
![Crete And Delete Task](demos/create_delete_task_test.gif) ![App Nav Test](demos/app

#### Testing Types Covered

The app covers following tests
1. Unit Tests
2. Integration Tests
3. End-to-End Tests

### Techniques Used

The makes use of the following techniques to test components in isolation and in conjuction with asynchronous logic.

1. Test doubles using fake resources
2. Test doubles using mocks (Mockito)
3. CountingIdlingResource for synchronization of background work with Espresso
4. DataBindingIdlingResource for synchronization of DataBinding related UI work with Espresso

### Libraries

Following Libraries were used for testing

1. JUnit4
2. Espresso
2. AndroidJUnit4 Runner
3. Roboelectric
4. Androidx Testing libries set
5. Kotlin Coroutes Testing library
6. Mockito
7. Hamcrest

### App Architecture Components Covered in this Repo


#### Unit Tests

1. ViewModel
2. Fragment
4. Room Database
5. Repository
6. Util classes

#### Integration Tests

1. ViewModel and Fragment pairs for all screens
2. LocalDataSource and Room Database
3. Fragment and Android Navigation Component
4. ViewModel and Repository

#### End-to-End Tests
1. Clicking task, edit it, save it
2. Create a task, delete it.
