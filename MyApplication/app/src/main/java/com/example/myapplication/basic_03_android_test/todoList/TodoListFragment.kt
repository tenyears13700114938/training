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
import com.example.myapplication.basic_03_android_test.TodoService.TodoOpMng
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoDetail.DETAIL_ACTIVITY_START_PARAM_TO_DO_INFO
import com.example.myapplication.basic_03_android_test.todoDetail.TodoDetailActivity
import com.example.myapplication.basic_03_android_test.todoList.TodoListActivity.Companion.EXTRA_PARAMETER_START_TYPE
import com.example.myapplication.util.copyTodo
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject

/**
 * A placeholder fragment containing a simple view.
 */
class TodoListFragment :  androidx.fragment.app.Fragment(), CoroutineScope by MainScope() {
    private lateinit var todoListView: RecyclerView
    private lateinit var todoListAdapter: TodoListAdapter
    @Inject
    lateinit var todoListViewModel: TodoListViewModel
    @Inject
    lateinit var todoViewModel: TodoViewModel
    @Inject
    lateinit var todoOpMng: TodoOpMng
    private val TAG = TodoListFragment::class.java.simpleName

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as TodoListActivity).todoListComponent.inject(this)
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_todo_list, container, false)
        todoListAdapter = TodoListAdapter(context!!).also {
            //it.clickItemEventSubject.debounce(100, TimeUnit.MILLISECONDS)
            it.clickItemEventSubject
                .subscribe() { _pair ->
                when (_pair.second.id) {
                    R.id.todo_delete_button -> {
                        deleteTodo(_pair.first)
                    }

                    R.id.todo_complete_button -> {
                        if (todoOpMng.isTodoEditing(_pair.first)) {
                            Toast.makeText(
                                this.requireContext(),
                                "Todo is Editing...",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            var copy = Todo()
                            copyTodo(_pair.first, copy)
                            copy.completed = if(copy.completed) false else true
                            todoOpMng.updateTodo(copy)
                        }
                    }

                    R.id.content_card -> {
                        if (todoOpMng.isTodoEditing(_pair.first)) {
                            Toast.makeText(
                                this.requireContext(),
                                "Todo is Editing...",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Intent(activity, TodoDetailActivity::class.java).apply {
                                val options = ActivityOptions.makeSceneTransitionAnimation(activity,_pair.second,"shared_todo_card")
                                putExtra(DETAIL_ACTIVITY_START_PARAM_TO_DO_INFO, _pair.first)
                                activity?.startActivity(this, options.toBundle())
                            }
                        }
                    }
                }
            }
            it.setHasStableIds(true)
        }
        todoListView = fragmentView.findViewById<RecyclerView>(R.id.todoList).also { _list ->
            _list.adapter = todoListAdapter
            _list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            _list.addItemDecoration(TodoListItemDecoration(_list, todoListAdapter))
        }

        /*todoListViewModel = activity?.run {
            TodoListViewModel.getTodoListViewModel(this,
                if(intent.getIntExtra(TodoListActivity.EXTRA_PARAMETER_START_TYPE, 0) == 1) StartType.search else StartType.all
            )
        } ?: throw Exception("Invalid activity")*/
        todoListViewModel.todoList.observe(this) {
            updateList(it, todoListViewModel.displayType.value)
        }
        todoListViewModel.displayType.observe(this){
            updateList(todoListViewModel.todoList.value, it)
        }

        /*todoViewModel = activity?.let {
            ViewModelProviders.of(it).get(TodoViewModel::class.java)
        } ?: throw Exception("no activity")*/

        fragmentView.findViewById<FloatingActionButton>(R.id.fab).also {_floatBtn ->
            _floatBtn.visibility = if(this@TodoListFragment.activity?.intent?.getIntExtra(EXTRA_PARAMETER_START_TYPE, 0) == 1) INVISIBLE else VISIBLE
            _floatBtn.setOnClickListener { view ->
                todoViewModel.todoInfo = Todo()
                val extras = FragmentNavigatorExtras(_floatBtn to "shared_todo_add")
                view.findNavController().navigate(R.id.todo_edit_navigation, null, null, extras)
            }
        }

        configureItemTouchHelper()
        return fragmentView
    }

    private fun deleteTodo(todo: Todo) {
        if (todoOpMng.isTodoEditing(todo)) {
            Toast.makeText(
                this.requireContext(),
                "Todo is Editing...",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            todoOpMng.deleteTodo(todo)
        }
    }

    private fun configureItemTouchHelper() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                                        ItemTouchHelper.UP or ItemTouchHelper.DOWN) {
            public override fun onMove(recyclerView : RecyclerView, viewHolder : RecyclerView.ViewHolder, target : RecyclerView.ViewHolder) : Boolean {
                val fromPos = viewHolder.getAdapterPosition();
                val toPos = target.getAdapterPosition();
                // move item in `fromPos` to `toPos` in adapter.
                Collections.swap(todoListAdapter.todoList, fromPos, toPos)
                todoListAdapter.notifyItemMoved(fromPos, toPos)
                return true;// true if moved, false otherwise
            }
            public override fun onSwiped(viewHolder : RecyclerView.ViewHolder, direction : Int) {
                // remove from adapter
                Log.d(TAG, "todolist onSwiped pos:" + viewHolder.adapterPosition)
                var todo = todoListAdapter.todoList[viewHolder.getAdapterPosition()]
                if(todo is Todo) {
                    deleteTodo(todo)
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(todoListView)
    }

    override fun onResume() {
        super.onResume()
        (activity as NavCommonActivity).run {
            if(intent.getIntExtra(EXTRA_PARAMETER_START_TYPE, 0) == 1){
                setMenuVisibility(false)
                setTitle("Attention Todos")
            }
            else {
                setMenuVisibility(true)
                setTitle("ToDo List")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    private fun updateList(list: List<Todo>?, displayType: Pair<ListDisplayType, ListDisplayType>?) {
        if (list != null && displayType != null && displayType.first != displayType.second) {
            when (displayType.second) {
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
        fun newInstance() : TodoListFragment {
            return TodoListFragment();
        }
    }
}
