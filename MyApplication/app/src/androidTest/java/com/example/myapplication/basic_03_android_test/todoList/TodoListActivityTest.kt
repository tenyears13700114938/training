package com.example.myapplication.basic_03_android_test.todoList

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.example.myapplication.DecoratedTodoLogic
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.model.TodoPriority
import com.example.myapplication.getOrAwaitValue
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

class TodoListActivityTest {

    lateinit var todoLogic: DecoratedTodoLogic

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    var activityRule: ActivityTestRule<TodoListActivity> =
        ActivityTestRule(TodoListActivity::class.java)

    val priorityList = mutableListOf<String>().apply {
        add(TodoPriority.EMERGENCY.name)
        add(TodoPriority.HIGH.name)
        add(TodoPriority.MIDDLE.name)
        add(TodoPriority.LOW.name)
    }

    @Before
    fun setUp() {
        if (activityRule.activity.todoLogic is DecoratedTodoLogic) {
            todoLogic = activityRule.activity.todoLogic as DecoratedTodoLogic
        }
    }

    @Test
    fun navToTitleEdit() {
        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.todo_title)).check(matches(isDisplayed()))
    }

    @Test
    fun navToTimeEdit() {
        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.next_button)).perform(click())
        onView(withId(R.id.datePicker)).check(matches(isDisplayed()))
    }

    @Test
    fun navToPhotoEdit() {
        onView(withId(R.id.fab)).perform(click())
        repeat(2) {
            onView(withId(R.id.next_button)).perform(click())
        }
        onView(withId(R.id.photo_disp_area)).check(matches(isDisplayed()))
    }

    @Test
    fun navFromMainToMain() {
        onView(withId(R.id.fab)).perform(click())
        repeat(2) {
            onView(withId(R.id.next_button)).perform(click())
        }
        repeat(3) {
            onView(withId(R.id.back_button)).perform(click())
        }
        onView(withId(R.id.fab)).check(matches(isDisplayed()))
    }


    @Test
    fun todoEdit() {
        val originalSize = todoLogic.todoLogic.todoRepository.alltodos.size
        //todo edit
        onView(withId(R.id.fab)).perform(click())

        //title
        onView(withId(R.id.todo_title)).perform(typeText("helloexpresso"))
        onView(withId(R.id.todo_description)).perform(typeText("good!"))
        onView(withId(R.id.todo_priority)).perform(click())
        onData(`is`(priorityList[3])).perform(click())
        onView(withId(R.id.next_button)).perform(click())

        //time
        onView(withId(R.id.next_button)).perform(click())

        //photoEdit
        onView(withId(R.id.next_button)).perform(click())

        /*val countDownLatch = CountDownLatch(1)
        todoLogic.countingIdlingResource.registerIdleTransitionCallback {
            val afterSize = todoLogic.todoLogic.todoRepository.alltodos.size
            assertThat(afterSize, `is`(originalSize + 1))
            countDownLatch.countDown()
        }
        countDownLatch.await()*/

        val todoListItemCntList =
            activityRule.activity.store.todoList.getOrAwaitValue(2, TimeUnit.MINUTES, 2) {}
        assertThat(originalSize, `is`(todoListItemCntList[0].size))
        assertThat(originalSize + 1, `is`(todoListItemCntList[1].size))
    }
}