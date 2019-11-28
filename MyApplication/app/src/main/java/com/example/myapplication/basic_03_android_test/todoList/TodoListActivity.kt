package com.example.myapplication.basic_03_android_test.todoList

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.core.view.iterator
import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoDetail.TodoDetailFragment
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository

import kotlinx.android.synthetic.main.activity_navi_common.*
import kotlinx.coroutines.*
import java.io.File

class TodoListActivity : NavCommonActivity(), CoroutineScope by MainScope() {
    private lateinit var todoListViewModel: TodoListViewModel
    private lateinit var todoViewModel: TodoViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.title = "ToDo List"
        mNavController.graph = mNavController.navInflater.inflate(R.navigation.todo_navigation)

        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel::class.java)
        todoListViewModel = ViewModelProviders.of(this).get(TodoListViewModel::class.java)
    }

    fun saveTodo(_todo: Todo) {
        launch {
            withContext(Dispatchers.IO) {
                todoRepository.getInstance(this@TodoListActivity).addToDo(_todo)
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        if(!TextUtils.isEmpty(todoViewModel.todoInfo.imageUrl)){
            File(todoViewModel.todoInfo.imageUrl!!).delete()
        }
        super.onDestroy()
        cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_todo_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_allItems ->{
                todoListViewModel.displayType.value?.also { oldValue ->
                    todoListViewModel.displayType.value = Pair(oldValue.second, ListDisplayType.all)
                }
                return true;
            }
            R.id.action_activeItems -> {
                todoListViewModel.displayType.value?.also { oldValue ->
                    todoListViewModel.displayType.value = Pair(oldValue.second, ListDisplayType.active)
                }
                return true
            }
            R.id.action_completeItems -> {
                todoListViewModel.displayType.value?.also { oldValue ->
                    todoListViewModel.displayType.value = Pair(oldValue.second, ListDisplayType.completed)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
