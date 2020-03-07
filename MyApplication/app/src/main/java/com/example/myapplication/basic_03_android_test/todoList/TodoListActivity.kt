package com.example.myapplication.basic_03_android_test.todoList

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.myapplication.MyApplication
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.TodoService.TodoOpMng
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoSearch.TodoSearchableActivity
import com.google.android.material.transition.MaterialContainerTransformSharedElementCallback
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.android.synthetic.main.activity_navi_common.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import java.io.File
import javax.inject.Inject

class TodoListActivity : NavCommonActivity(), CoroutineScope by MainScope() {
    @Inject
    lateinit var todoListViewModel: TodoListViewModel
    @Inject
    lateinit var todoViewModel: TodoViewModel
    @Inject
    lateinit var todoOpMng: TodoOpMng

    lateinit var todoListComponent: TodoListComponent

    companion object {
        val EXTRA_PARAMETER_START_TYPE = "EXTRA_PARAMETER_START_TYPE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        todoListComponent =
            (application as MyApplication).appComponent.registrationComponent().create(this, intent)
                .apply {
                    inject(this@TodoListActivity)
                }

        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
        
        super.onCreate(savedInstanceState)

        toolbar.title = if(intent.getIntExtra(EXTRA_PARAMETER_START_TYPE, 0) == 0)"ToDo List" else "Attention Todos"
        mNavController.graph = mNavController.navInflater.inflate(R.navigation.todo_navigation)

        //todoViewModel = ViewModelProviders.of(this).get(TodoViewModel::class.java)
        /*todoListViewModel = TodoListViewModel.getTodoListViewModel(this,
            if(intent.getIntExtra(TodoListActivity.EXTRA_PARAMETER_START_TYPE, 0) == 1) StartType.search else StartType.all
            )*/
    }

    fun saveTodo(_todo: Todo) {
        todoOpMng.addTodo(_todo)
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

        val searchItem = menu?.findItem(R.id.action_search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as  SearchManager
        val searchView = searchItem?.actionView as androidx.appcompat.widget.SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(ComponentName(this,TodoSearchableActivity::class.java)))

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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}

enum class StartType(var type : Int) {
    all(0),
    search(1)
}
