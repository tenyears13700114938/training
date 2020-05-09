package com.example.myapplication.util.dagger

import android.app.Service
import android.content.Context
import android.hardware.SensorManager
import android.view.LayoutInflater
import com.example.myapplication.MyApplication
import com.example.myapplication.basic_03_android_test.TodoService.ITodoLogic
import com.example.myapplication.basic_03_android_test.TodoService.TodoLogic
import com.example.myapplication.basic_03_android_test.todoRepository.ITodoRepository
import com.example.myapplication.basic_03_android_test.todoRepository.todoDao
import com.example.myapplication.basic_03_android_test.todoRepository.todoDatabase
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class AppModule {
    @Module
    companion object {
        @Provides
        @Singleton
        @JvmStatic
        fun providesSensorManager(app: MyApplication): SensorManager {
            return app.getSystemService(Service.SENSOR_SERVICE) as SensorManager
        }

        @Provides
        @Singleton
        @JvmStatic
        fun providesLayoutInflater(app: MyApplication): LayoutInflater {
            return LayoutInflater.from(app)
        }

        @Provides
        @Singleton
        @JvmStatic
        fun providesContext(app: MyApplication): Context {
            return app
        }

        @Provides
        @Singleton
        @JvmStatic
        fun providesTodoDataBase(context : Context) : todoDatabase{
            return todoDatabase.getInstance(context)
        }

        @Provides
        @Singleton
        @JvmStatic
        fun providesTodoDao(todoDatabase: todoDatabase) : todoDao {
            return todoDatabase.todoDao()
        }
    }
    @Binds
    @Singleton
    abstract fun providesTodoRepository(todoRepository: todoRepository) : ITodoRepository

    @Binds
    @Singleton
    abstract fun providesTodoLogic(todoLogic: TodoLogic) : ITodoLogic
}