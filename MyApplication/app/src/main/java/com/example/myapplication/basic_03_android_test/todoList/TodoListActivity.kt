package com.example.myapplication.basic_03_android_test.todoList

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.SearchView
import androidx.core.view.iterator
import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.TodoService.TodoOpMng
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoDetail.TodoDetailFragment
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository
import com.example.myapplication.basic_03_android_test.todoSearch.TodoSearchableActivity

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
        TodoOpMng.getIns(applicationContext).addTodo(_todo)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
        todoViewModel.todoInfo.imageUrl?.also {
            File(it).delete()
        }
        todoViewModel.todoInfo.reset()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_todo_list, menu)

        var searchItem = menu?.findItem(R.id.action_search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as  SearchManager
        val searchView = searchItem?.actionView as androidx.appcompat.widget.SearchView
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(ComponentName(this,TodoSearchableActivity::class.java)))

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
