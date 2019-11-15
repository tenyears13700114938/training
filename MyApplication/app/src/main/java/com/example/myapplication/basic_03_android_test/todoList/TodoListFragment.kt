package com.example.myapplication.basic_03_android_test.todoList

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository
import kotlinx.coroutines.*
import java.util.stream.Collectors

/**
 * A placeholder fragment containing a simple view.
 */
class TodoListFragment : Fragment(), CoroutineScope by MainScope() {
    private lateinit var todoListView: RecyclerView
    private lateinit var todoListAdapter: TodoListAdapter
    private lateinit var todoListViewModel: TodoListViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var fragmentView = inflater.inflate(R.layout.fragment_todo_list, container, false)
        todoListAdapter = TodoListAdapter(context!!).also {
            it.clickSubject.subscribe() { _todo ->
                if (!_todo.completed) {
                    _todo.completed = true
                    launch {
                        withContext(Dispatchers.IO) {
                            todoRepository.getInstance(this@TodoListFragment.context!!)
                                .updateToDo(_todo)
                        }
                    }
                }
            }
        }
        todoListView = fragmentView.findViewById<RecyclerView>(R.id.todoList).also { _list ->
            _list.adapter = todoListAdapter
            _list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

        todoListViewModel = activity?.run {ViewModelProviders.of(this).get(TodoListViewModel::class.java)} ?: throw Exception("Invalid activity")
        todoListViewModel.todoList.observe(this) {
            updateList(it, todoListViewModel.displayType.value)
        }
        todoListViewModel.displayType.observe(this){
            updateList(todoListViewModel.todoList.value, it)
        }

        return fragmentView
    }

    override fun onResume() {
        super.onResume()
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
