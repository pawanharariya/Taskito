# Testing on Android #

## Source Sets and Dependencies ##

The source code is divided in 3 folders called **source sets**.

1. Main Source Set - It contains all the app code.

2. Test source set - It contains local tests, that run on local machine (i.e. laptop) and don't
   require an emulator or a physical device to run.

3. androidTest source set - It contains Instrumented Tests, that run on real or emulated devices.
   They require Android Framework to get objects which are possible only on a running device such
   as `context` or `application`.

The testing code knows about the classes in the main source set, so that it can test those classes
using all the required objects that the non-test class has access to. Also, the test file structure
should match the main source code file structure for similar reasons.

But the code in the main source set does not know anything about the testing code. And the two test
source sets don't know about each other. Also, when the production APK is uploaded to Google Play
Store, none of the testing code is included.

Dependencies in Gradle are scoped by source set:

* `implementation`- for the main source set
* `testImplementation`- for the test source set
* `androidTestImplementation`- for the androidTest source set

## Testing ViewModel with AndroidX Test and Robolectric ##

AndroidX Test is a collection of libraries for testing. It provides test versions of Android
components like applications and activities. It can be used to run the same test as Unit Test and as
Instrumented Test. It is used in combination with Robolectric library that creates a simulated
Android environment specifically for local tests.

## Testing LiveData ##

To test LiveData we have to use architecture component's `InstantTaskExecutorRule()` and Observe
LiveData. LiveData does not do much until it is observed, especially in case of Transformations.
Hence we need to observe LiveData to get expected behaviour. But in testing class there isn't a
LifeCycleOwner such as Activity or Fragment with which to observe the LiveData. So we can
use `observeForever` on the LiveData and then `removeObserver` to avoid observer leak. To avoid
boilerplate code, we can create a extension function on LiveData to handle this, as
mentioned [here](https://medium.com/androiddevelopers/unit-testing-livedata-and-other-common-observability-problems-bb477262eb04).

### InstanceTaskExecutorRule ###

InstanceTaskExecutorRule is a JUnit rule. JUnit rules are classes that allows to define some code
that runs before and after each test run. This role run all architecture components related
background jobs on the same thread. This ensures that the test results happens synchronously and in
a repeatable order.

## Test Doubles ##

Test Double is a version of a class crafted specifically for testing. We make test doubles of
dependencies. For example in case of repository we make test doubles for data sources, so that we
only unit test the code related to repository. Two types of test doubles are explained below :

1. Fake - A test double that has a "working" implementation of the class, but it's implemented in a
   way that makes it good for tests but unsuitable for production. Like instead of calling the real
   database we can use a HashMap implementation as a fake data source, for testing purpose.

2. Mock - A test double that tracks which of its methods are called. Mocks pass or fail a test
   depending on whether their methods were called correctly.

## Integration Testing of Fragment-ViewModel Pair ##

FragmentScenario - It is an API from the AndroidX test library to create fragments for testing
purposes. It gives control over starting state and lifecycle of the fragment. It can be used in both
local and instrumented tests.

Service Locators - In some situations constructor dependency injection is not easily doable and
difficult to set up, such as injecting objects in activities and fragments. It is because there is
no easy way to change the constructors of activities and fragments. In such cases, we use setter
dependency injection or the service locator pattern.

A service locator is a singleton whose purpose is to store and provide dependencies. It does this
for both the source code and for the test code. The benefit of using a service locator over setter
dependency injection is that with setter dependency injection, we need to set the correct dependency
each time we make an instance of the class. But with service locator we set all correct dependencies
in the beginning and then they are used everywhere.

## Testing UI using Espresso ##

Espresso is an Android UI testing library. Using Espresso, we can interact with views and check
their state. The four parts that make up an Espresso statement are :

1. Static Espresso method - It starts an espresso statement and defines which part of UI to
   interact with. For example : `onView()` method states - we are going to do something with view.

2. ViewMatcher - The ViewMatcher finds the required view in the UI. For example `withId()` finds a
   view that matches the given ID. We need to uniquely identify the views for Espresso, if a
   ViewMatcher identifies more than one view that matches the condition, we get
   an `AmbiguousViewMatcherException`.

3. ViewAction - It is something that can be performed on the view. For example `perform(click())`
   performs a click on the view.

4. ViewAssertion - It asserts something about the view. For example `matches(isChecked())` asserts
   that the view is checked such as a check-box.

   ```
   onView(withId(R.id.task_complete_checkbox))
   .perform(click())
   .check(matches(isChecked()))
   ```

## Testing navigation using Mockito ##

Mock : It is type of test double that knows whether its methods were called correctly.

### Avoiding misuse of mocks : ###

During testing we usually check that after doing some actions, the ending state is correct. For
example, calling a method and then seeing if some value is changed, or press a button and see if
some text changes.

Mocks have a possibility of being misused to test implementation instead of a state change. For
example, we might end up testing that bunch of methods were called, but not anything actually
changed.

```
// Mock test example
myFragmentMock.changeMyUI()

// only verifies that updateText method was called
verify(myFragmentMock).updateText(“changed”)


// No mock test example
myFragment.changeMyUI()
// verifies that correct text is displayed
assertEquals(myFragment.textView.text, “changed”)
```

The no mock test is better in this case for two reasons.

1. Mock test doesn't check actual state change. For example if the `updateText()`method is broken,
   it gets called but doesn't update the text. The mock test still passes.

2. Mock version of test is more implementation dependent. If the code changes such as we no longer
   need to call `updateText()`, the mock test will fail, even if maybe some other way the text gets
   updated.

So, there are chances of false positives and false failures when mocking things.

Use mocks when there isn't a clear state change or testing that state change is tricky in some way.
Navigation is one such example. Since in this case we don't test whether the actual screen/fragment
to navigate to, is loaded up with correct details, as it is outside the scope of current fragment
test. We only check the correct navigation method was called with correct arguments. We mock the
navigation controller for navigation.

Why testing navigation is perfect for mock -

1. End state change i.e. opening the other fragment is outside the scope of code currently being
   tested.

2. The mocked navigation controller API, is unlikely to change. If it changes it will break the
   tests.

Mockito Library is framework for making test doubles such as mocks, stubs and spies.

## Testing navigation above Navigation 2.2.1 ##

NOTE : when using Navigation 2.2.1 or earlier, it is recommended to use a mock NavController with
Mockito and verify that the correct actions are taken rather than verify the NavController's state.

With the latest Navigation API, we don't need to use mocks anymore, we can now directly test the
state of navController with `TestNavHostController`.

## Testing Asynchronous Code ##

Things to be kept in mind when testing asynchronous code :

1. Asynchronous code is non-deterministic - If we run operation A and operation B in parallel,
   and if we do this multiple times, we can't be sure whether A will finish first or B will finish
   first. This can cause flaky test, if the tests assume that one operation finishes before another.

2. Testing Asynchronous code requires synchronisation mechanism - When a test running on test thread
   run tasks on different threads or make new coroutines. These coroutines run asynchronously in
   parallel and the test may finish before the other tasks finish. So if we assert some value from
   the long running operation, the test will fail as it hasn't waited long enough for them to finish
   before checking the end result. Synchronisation mechanisms are ways to tell the test execution to
   wait until the asynchronous work finishes.

### `runTest` ###

`runTest` is a coroutine builder designed for testing. It ensures that the test blocks waits until
all of the coroutines it starts are finished. It runs the coroutines in a deterministic order. It
uses a special `UnconfinedTestDispatcher` to run and suspend functions in `runTest`.

## Writing coroutine tests that use viewModelScope ##

ViewModel coroutines, by default use `Dispatchers.Main` because often ViewModels manipulate the UI.
But with tests it throws an `IllegalStateException`, because `Dispatchers.Main` uses Android's main
Looper, which is not available while running local tests.

To solve this we use `UnconfinedTestDispatcher` instead. By swapping it with `Dispatchers.Main`
using `Dispatchers.setMain()` method. However, after each test we must reset it.

### Injecting Dispatchers ###

When writing local tests, we should use a single TestDispatcher instead of making multiple
TestDispatchers. If we ever switch dispatchers in the code under test, for example using
Dispatchers.IO in repository. And, if the IO dispatcher starts a different dispatcher from the test
dispatcher, the coroutines no longer run deterministically and in blocking fashion. So we should
avoid hard-coding the dispatcher and instead use dependency injection to pass in the dispatcher.
This way we can easily swap in the test dispatcher for our tests.

**JUnit Rules** - These are classes where we define generic testing code that can execute before,
after or during tests. It is generally the code in `@Before` and `@After` blocks, put into a class
so that it can easily be re-used.

## Testing Coroutine Timing ##

The TestDispatcher executes the coroutine deterministically and immediately, which is what we want
most of the time, as it makes the tests run fast. But sometimes, we want to check the state in the
middle.

For example, checking the state of a loading indicator before the coroutine starts and after it
finishes. But with TestDispatcher the coroutine finishes before any assert statements, hence the
test fails.

NOTE : This section is incomplete as PauseDispatcher and ResumeDispatcher are deprecated, so will
continue this once we migrate to using StateFlow instead of LiveData.

## Testing Error Handling ##

Apart from testing correct behaviour of the code, it is also important to test scenarios when
something get wrong like an error or some edge case. For example, when network is down.

In such cases the test double should be implemented to return an error. For fakes, we can create an
error flag, which determines whether or not the fake should return error. Then we can write a test
for that error.

## Testing ROOM ##

DAO Unit tests should generally be instrumented tests. Because, if we run these tests locally they
will use the SQLite version available on the local machine, and that could be very different than
the SQLite version that ships with the Android device.

To test DAO, we create the room database using an in-memory database builder, as this database will
be deleted once the process is killed.

## Idling Resources and End-to-End Testing ##

End-to-end tests are also called black box tests, because they don't know or not really supposed to
know, how things are implemented internally. They only check the outcome is right, given the input.

### Espresso for end-to-end testing ##

Espresso allows to interact with UI and then check that the UI state is updated. Espresso has
built-in synchronisation mechanisms, so we don't have to write any code to wait for the click to
finish and for UI to update.

### Idling Resources ###

Espresso synchronisation mechanism, usually know and wait for the events between button click and UI
state update. However, for complex cases, where we end up jumping to another co-routine or thread
that Espresso doesn't know about, we need to provide our own synchronisation mechanism, using Idling
Resource.

Idling Resource is a synchronisation mechanism which tracks whether the application is busy or idle
for Espresso. If the application is idle, Espresso can continue testing. But, if the application is
working, Espresso will wait until application becomes idle.

CountingIdlingResource :
It allows to increment and decrement a counter such that when counter is greater than zero, the app
is considered working, and when the counter is less that zero that app is considered idle.

## Helpful Tips ##

1. To make tests more readable, following the below naming convention
   ```
   subjectUnderTest_actionOrInput_resultState
   ```

2. The test should be structured in Arrange, Act, Assert (AAA) format.

3. For Unit Testing a ViewModel, we can directly instantiate the ViewModel, which otherwise is
   provided by ViewModelFactory.

4. Avoid top level initialisations because it will cause the same instance to be used for all tests.
   This is something that should avoid because each test should have a fresh instance of the subject
   under test (for example ViewModel), instead declare a top-level variable but initialise it
   in `@Before block`, this block runs before every test, hence each test will get a fresh instance.

5. When we test fragment and use `launchFragmentInContainer`, it launches the fragment in an empty
   activity. Since fragments usually inherit their theme from the activity, we need to pass the
   theme as parameter.

6. For Espresso UI testing, it's best practice to turn animations off. It also makes tests to run
   faster.

7. Different Android devices are shipped with different versions of SQLite, so the Dao Unit tests
   should be run as instrumented tests and should be tested on different devices. 
 