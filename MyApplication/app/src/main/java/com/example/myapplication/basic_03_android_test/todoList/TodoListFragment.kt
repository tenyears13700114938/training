package com.example.myapplication.basic_03_android_test.todoList

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.Flux.CoroutineDispatcher
import com.example.myapplication.basic_03_android_test.Flux.TodoActionCreator
import com.example.myapplication.basic_03_android_test.Flux.TodoStore
import com.example.myapplication.basic_03_android_test.TodoService.TodoLogic
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.model.TodoEvent
import com.example.myapplication.basic_03_android_test.todoDetail.DETAIL_ACTIVITY_START_PARAM_TO_DO_INFO
import com.example.myapplication.basic_03_android_test.todoDetail.TodoDetailActivity
import com.example.myapplication.basic_03_android_test.todoList.TodoListActivity.Companion.EXTRA_PARAMETER_START_TYPE
import com.example.myapplication.basic_03_android_test.uiCommon.CardEvent
import com.example.myapplication.databinding.FragmentTodoListBinding
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.*
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject

/**
 * A placeholder fragment containing a simple view.
 */
class TodoListFragment : DaggerFragment(), CoroutineScope by MainScope() {
    private lateinit var binding: FragmentTodoListBinding
    private lateinit var todoListAdapter: TodoListAdapter

    @Inject
    lateinit var todoLogic: TodoLogic
    private val TAG = TodoListFragment::class.java.simpleName

    @Inject
    lateinit var dispatcher: CoroutineDispatcher

    @Inject
    lateinit var actionCreator: TodoActionCreator

    @Inject
    lateinit var todoStore: TodoStore

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /*(activity as TodoListActivity).todoListComponent.inject(this)*/
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoListBinding.inflate(inflater, container, false)
        todoListAdapter = TodoListAdapter(context!!).also {
            it.clickItemEventSubject
                .subscribe() { cardEventInfo ->
                    when (cardEventInfo.cardEvent) {
                        CardEvent.STATUS_CHANGE -> {
                            dispatcher.dispatch(actionCreator.updateTodo(cardEventInfo.todo))
                        }

                        CardEvent.SELECTED -> {
                            onCardSelected(cardEventInfo)
                        }
                    }
                }
            it.setHasStableIds(true)
        }
        binding.todoList.apply {
            adapter = todoListAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(TodoListItemDecoration(this, todoListAdapter))
        }

        /*todoListViewModel = activity?.run {
            TodoListViewModel.getTodoListViewModel(this,
                if(intent.getIntExtra(TodoListActivity.EXTRA_PARAMETER_START_TYPE, 0) == 1) StartType.search else StartType.all
            )
        } ?: throw Exception("Invalid activity")*/

        binding.fab.apply {
            visibility = if (this@TodoListFragment.activity?.intent?.getIntExtra(
                    EXTRA_PARAMETER_START_TYPE,
                    0
                ) == 1
            ) INVISIBLE else VISIBLE
            setOnClickListener { view ->
                todoStore.editingTodo.observe(this@TodoListFragment) {}
                val extras = FragmentNavigatorExtras(this to "shared_todo_add")
                view.findNavController().navigate(R.id.todo_edit_navigation, null, null, extras)
            }
        }

        configureItemTouchHelper()
        return binding.root
    }

    fun onCardSelected(cardEventInfo: TodoEvent) {
        if (todoLogic.isTodoEditing(cardEventInfo.todo)) {
            Toast.makeText(
                this.requireContext(),
                "Todo is Editing...",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Intent(activity, TodoDetailActivity::class.java).apply {
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    activity,
                    cardEventInfo.refView,
                    "shared_todo_card"
                )
                putExtra(DETAIL_ACTIVITY_START_PARAM_TO_DO_INFO, cardEventInfo.todo)
                activity?.startActivity(this, options.toBundle())
            }
        }
    }

    private fun deleteTodo(todo: Todo) {
        if (todoLogic.isTodoEditing(todo)) {
            Toast.makeText(
                this.requireContext(),
                "Todo is Editing...",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            launch {
                withContext(Dispatchers.Default) {
                    todoLogic.deleteTodo(todo)
                }
            }
        }
    }

    private fun configureItemTouchHelper() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            ItemTouchHelper.UP or ItemTouchHelper.DOWN
        ) {
            public override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = viewHolder.getAdapterPosition();
                val toPos = target.getAdapterPosition();
                // move item in `fromPos` to `toPos` in adapter.
                Collections.swap(todoListAdapter.todoList, fromPos, toPos)
                todoListAdapter.notifyItemMoved(fromPos, toPos)
                return true;// true if moved, false otherwise
            }

            public override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // remove from adapter
                Log.d(TAG, "todolist onSwiped pos:" + viewHolder.adapterPosition)
                var todo = todoListAdapter.todoList[viewHolder.getAdapterPosition()]
                if (todo is Todo) {
                    deleteTodo(todo)
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.todoList)
    }

    @ExperimentalCoroutinesApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("DebugCoroutine", "onActiviy result...")
        dispatcher.dispatch(actionCreator.loadedTodoList)
        dispatcher.dispatch(actionCreator.todoListDisplayType(ListDisplayType.all))

        todoStore.todoList.observe(this) {
            Log.d("DebugCoroutine", "update...")
            updateList(it, todoStore.todoListDisplayType.value)
        }

        todoStore.addTodoResult.observe(this) {
        }

        todoStore.todoListDisplayType.observe(this) {
            updateList(todoStore.todoList.value, it)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as NavCommonActivity).run {
            if (intent.getIntExtra(EXTRA_PARAMETER_START_TYPE, 0) == 1) {
                setMenuVisibility(false)
                setTitle("Attention Todos")
            } else {
                setMenuVisibility(true)
                setTitle("ToDo List")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    private fun updateList(
        list: List<Todo>?,
        displayType: ListDisplayType?
    ) {
        if (list != null && displayType != null) {
            when (displayType) {
                ListDisplayType.all -> {
                    todoListAdapter.updateList(list)
                }
                ListDisplayType.active -> {
                    todoListAdapter.updateList(list.stream().filter { _todo -> !_todo.completed }
                        .collect(Collectors.toList()))

                }
                ListDisplayType.completed -> {
                    todoListAdapter.updateList(list.stream().filter() { _todo -> _todo.completed }
                        .collect(Collectors.toList())
                    )
                }
                else -> {
                }
            }
        }
    }

    companion object {
        fun newInstance(): TodoListFragment {
            return TodoListFragment();
        }
    }
}
