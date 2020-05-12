package com.example.myapplication.basic_03_android_test.Flux

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.util.MainCoroutineRule
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

data class DumpObject(val id: Int) : Action

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class CoroutineDispatcherTest {
    lateinit var testIns: CoroutineDispatcher

    val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    val rule = MainCoroutineRule()

    @Before
    fun setUp() {
        testIns = CoroutineDispatcher()
    }

    @Test
    fun dispatch() {
        runBlocking {
            launch {
                testIns.subScribe<DumpObject>().apply {
                    repeat(5) {
                        assertThat(receive(), `is`(DumpObject(it)))
                    }
                }
            }

            launch {
                //var receiveChannel = testIns.subScribe<DumpObject>()
                repeat(5) {
                    testIns.dispatch(ActionCreate {
                        DumpObject(it)
                    })
                    delay(100)
                }
            }
        }
    }
}