package com.example.myapplication

import android.app.Service.SENSOR_SERVICE
import android.content.Context
import android.hardware.SensorManager
import android.view.LayoutInflater
import androidx.test.espresso.idling.CountingIdlingResource
import com.example.myapplication.basic_03_android_test.TodoService.ITodoLogic
import com.example.myapplication.basic_03_android_test.todoRepository.ITodoRepository
import com.example.myapplication.basic_03_android_test.todoRepository.todoDao
import com.example.myapplication.basic_03_android_test.todoRepository.todoDatabase
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class TestAppModule {
    @Module
    companion object {
        @Provides
        @Singleton
        @JvmStatic
        fun providesSensorManager(app: TestMyApplication): SensorManager {
            return app.getSystemService(SENSOR_SERVICE) as SensorManager
        }

        @Provides
        @Singleton
        @JvmStatic
        fun providesLayoutInflater(app: TestMyApplication): LayoutInflater {
            return LayoutInflater.from(app)
        }

        @Provides
        @Singleton
        @JvmStatic
        fun providesContext(app: TestMyApplication): Context {
            return app
        }

        @Provides
        @Singleton
        @JvmStatic
        fun providesTodoDataBase(context: Context): todoDatabase {
            return todoDatabase.getInstance(context)
        }

        @Provides
        @Singleton
        @JvmStatic
        fun providesTodoDao(todoDatabase: todoDatabase): todoDao {
            return todoDatabase.todoDao()
        }

        @Provides
        @Singleton
        @JvmStatic
        fun provideCountingIdlingResource() = CountingIdlingResource("todoLogic")
    }

    @Binds
    @Singleton
    abstract fun providesTodoRepository(todoRepository: todoRepository): ITodoRepository

    @Binds
    @Singleton
    abstract fun providesTodoLogic(todoLogic: DecoratedTodoLogic): ITodoLogic
}