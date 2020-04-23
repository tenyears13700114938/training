package com.example.myapplication.basic_03_android_test.todoList

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.observe
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.Flux.CoroutineDispatcher
import com.example.myapplication.basic_03_android_test.Flux.TodoActionCreator
import com.example.myapplication.basic_03_android_test.Flux.TodoListStore
import com.example.myapplication.basic_03_android_test.TodoService.TodoLogic
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoSearch.TodoSearchableActivity
import com.google.android.material.transition.MaterialContainerTransformSharedElementCallback
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_navi_common.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class TodoListActivity : NavCommonActivity(), HasAndroidInjector, CoroutineScope by MainScope() {
    @Inject
    lateinit var todoLogic: TodoLogic

    @Inject
    lateinit var dispatcher: CoroutineDispatcher

    @Inject
    lateinit var todoActionCreator: TodoActionCreator

    @Inject
    lateinit var store: TodoListStore

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    companion object {
        val EXTRA_PARAMETER_START_TYPE = "EXTRA_PARAMETER_START_TYPE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        /*todoListComponent =
            (application as MyApplication).appComponent.registrationComponent().create(this, intent)
                .apply {
                    inject(this@TodoListActivity)
                }*/
        AndroidInjection.inject(this)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false

        super.onCreate(savedInstanceState)

        toolbar.title = if (intent.getIntExtra(
                EXTRA_PARAMETER_START_TYPE,
                0
            ) == 0
        ) "ToDo List" else "Attention Todos"
        mNavController.graph = mNavController.navInflater.inflate(R.navigation.todo_navigation)
    }

    fun saveTodo(_todo: Todo) {
        launch {
            todoLogic.addTodo(_todo)
        }
    }

    override fun onResume() {
        super.onResume()
        dispatcher.dispatch(todoActionCreator.loadedTodoList)
    }

    override fun onDestroy() {
        store.editingTodo.observe(this) {
            it.imageUrl?.let {
                File(it).delete()
            }
        }
        super.onDestroy()
        cancel()

        /*todoViewModel.todoInfo.imageUrl?.also {
            File(it).delete()
        }
        todoViewModel.todoInfo.reset()*/
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_todo_list, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = searchItem?.actionView as androidx.appcompat.widget.SearchView
        searchView.setSearchableInfo(
            searchManager.getSearchableInfo(
                ComponentName(
                    this,
                    TodoSearchableActivity::class.java
                )
            )
        )

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_allItems -> {
                dispatcher.dispatch(todoActionCreator.todoListDisplayType(ListDisplayType.all))
                return true;
            }
            R.id.action_activeItems -> {
                dispatcher.dispatch(todoActionCreator.todoListDisplayType(ListDisplayType.active))
                return true
            }
            R.id.action_completeItems -> {
                dispatcher.dispatch(todoActionCreator.todoListDisplayType(ListDisplayType.completed))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }
}

enum class StartType(var type: Int) {
    all(0),
    search(1)
}
