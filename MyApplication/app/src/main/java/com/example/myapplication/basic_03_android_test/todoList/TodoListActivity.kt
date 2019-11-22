package com.example.myapplication.basic_03_android_test.todoList

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.iterator
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository
import com.example.myapplication.util.searchViewUtil
import com.google.android.material.navigation.NavigationView

import kotlinx.android.synthetic.main.activity_todo_list.*
import kotlinx.coroutines.*
import java.io.File

class TodoListActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var todoListViewModel: TodoListViewModel
    private lateinit var todoViewModel: TodoViewModel
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
        configureNavView(mNaviView)

        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel::class.java)
        todoListViewModel = ViewModelProviders.of(this).get(TodoListViewModel::class.java)
    }

    private fun configureNavView(navigationView: NavigationView?) {
        navigationView?.let {
            val drawerLayoutParams : DrawerLayout.LayoutParams = it.layoutParams as DrawerLayout.LayoutParams
            drawerLayoutParams.width = resources.displayMetrics.widthPixels * 5 / 6
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
            var menuIterator = it.menu.iterator()
            while (menuIterator.hasNext()) {
                menuIterator.next().let {
                    it.setOnMenuItemClickListener() { item ->
                        when (item.itemId) {
                            R.id.todo -> true
                            R.id.nasa_world -> true
                            R.id.next_what ->  true
                            R.id.close_menu_item -> {
                                findViewById<DrawerLayout>(R.id.root_drawer).closeDrawer(
                                    GravityCompat.START
                                )
                                true
                            }
                            else -> true
                        }
                    }
                }
            }
        }
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

    fun setMenuVisibility(visibility : Boolean){
        toolbar.menu.iterator().let { _iterator ->
            while(_iterator.hasNext()){
                _iterator.next().setVisible(visibility)
            }
        }
    }

    fun setTitle(title : String){
        toolbar.title = title
    }

}
