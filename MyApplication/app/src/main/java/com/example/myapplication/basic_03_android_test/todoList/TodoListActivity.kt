package com.example.myapplication.basic_03_android_test.todoList

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.addTodo.TodoAddActivity
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository
import com.example.myapplication.util.searchViewUtil
import com.google.android.material.navigation.NavigationView

import kotlinx.android.synthetic.main.activity_todo_list.*
import kotlinx.coroutines.*

class TodoListActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var todoListViewModel: TodoListViewModel
    private lateinit var editingTodoViewModel: TodoViewModel
    private lateinit var mNaviView : NavigationView
    private lateinit var mNavController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)

        val host = supportFragmentManager.findFragmentById(R.id.navi_host_fragment) as NavHostFragment? ?: return
        mNavController = host.navController

        //configure toolbar
        toolbar.setNavigationIcon(R.drawable.navi_menu)
        toolbar.title = "ToDo List"
        toolbar.setTitleTextColor(resources.getColor(R.color.colorWhite, null))
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            findViewById<DrawerLayout>(R.id.root_drawer).openDrawer(GravityCompat.START)
        }

        //configure navigationView
        mNaviView = findViewById(R.id.navi_slide_menu)
        configureNaviView(mNaviView)

        todoListViewModel = ViewModelProviders.of(this).get(TodoListViewModel::class.java)
        editingTodoViewModel = ViewModelProviders.of(this).get(TodoViewModel::class.java)
    }

    private fun configureNaviView(navigationView: NavigationView?) {
        navigationView?.let {
            val drawerLayoutParams : DrawerLayout.LayoutParams = it.layoutParams as DrawerLayout.LayoutParams
            drawerLayoutParams.width = resources.displayMetrics.widthPixels / 2
            it.layoutParams = drawerLayoutParams

            searchViewUtil(
                it.getHeaderView(0) as ViewGroup,
                R.id.navi_slide_menu_image
            )?.also { _imageView ->
                if (_imageView is ImageView) {
                    val imageViewLayoutParams: LinearLayout.LayoutParams =
                        _imageView.layoutParams as LinearLayout.LayoutParams
                    imageViewLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                    _imageView.layoutParams = imageViewLayoutParams
                    Glide.with(this).load(R.drawable.children_1822688_960_720)
                        .into(_imageView)
                }
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
