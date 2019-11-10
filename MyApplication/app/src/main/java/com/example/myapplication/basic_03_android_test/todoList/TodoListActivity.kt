package com.example.myapplication.basic_03_android_test.todoList

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.addTodo.TodoAddActivity
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository

import kotlinx.android.synthetic.main.activity_todo_list.*
import kotlinx.coroutines.*

class TodoListActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var todoListViewModel: TodoListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)
        toolbar.setNavigationIcon(R.drawable.navi_menu)
        toolbar.title = "ToDo List"
        toolbar.setTitleTextColor(resources.getColor(R.color.colorWhite, null))
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            findViewById<DrawerLayout>(R.id.root_drawer).openDrawer(GravityCompat.START)
        }

        todoListViewModel = ViewModelProviders.of(this).get(TodoListViewModel::class.java)

        fab.setOnClickListener { view ->
            Intent(this, TodoAddActivity::class.java).let { _intent ->
                startActivityForResult(_intent, TodoAddActivity.Request_Code_Add_ToDo)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            data.getSerializableExtra(TodoAddActivity.ToDo_Extra_Parameter)?.also {_todo ->
                if(_todo is Todo){
                    launch {
                        withContext(Dispatchers.IO) {
                            todoRepository.getInstance(this@TodoListActivity).addToDo(_todo)
                        }
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
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
