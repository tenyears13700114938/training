package com.example.myapplication.basic_03_android_test.todoSearch

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MyApplication
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.TodoService.OpResult
import com.example.myapplication.basic_03_android_test.TodoService.TodoOpMng
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoDetail.DETAIL_ACTIVITY_START_PARAM_TO_DO_INFO
import com.example.myapplication.basic_03_android_test.todoDetail.TodoDetailActivity
import com.example.myapplication.basic_03_android_test.todoList.TodoListAdapter
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository
import com.example.myapplication.basic_03_android_test.tooBroadcastReceiver.todoBroadcastReceiver
import com.example.myapplication.util.copyTodo
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.exceptions.Exceptions
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class TodoSearchableFragment : androidx.fragment.app.Fragment() , CoroutineScope by MainScope(){
    companion object {
        private const val EXTRA_SEARCH_KEY = "EXTRA_SEARCH_KEY"
        fun newInstance(searchInfo : String) =
            TodoSearchableFragment().let {
                val bundle = Bundle()
                bundle.putString(EXTRA_SEARCH_KEY, searchInfo)
                it.arguments = bundle
                it
            }
    }
    @Inject
    lateinit var todoRepository: todoRepository
    @Inject
    lateinit var todoOpMng: TodoOpMng
    private lateinit var viewModel: TodoSearchableViewModel
    private lateinit var listView : RecyclerView
    private lateinit var listAdapter : TodoListAdapter
    private lateinit var noresultTextView : TextView
    private val compositeDisposable = CompositeDisposable()

    override fun onAttach(context: Context) {
        (context.applicationContext as? MyApplication)?.appComponent?.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.todo_searchable_fragment, container, false)
        initListView(rootView)
        noresultTextView = rootView.findViewById(R.id.no_result_textview)
        return rootView
    }

    private fun initListView(rootView: View?) {
        listView = rootView?.findViewById<RecyclerView>(R.id.search_todo_list) ?: throw Throwable("not search todo list")
        listAdapter = TodoListAdapter(context!!)
        listView.also {
            it.layoutManager = LinearLayoutManager(it.context, LinearLayoutManager.VERTICAL, false)
            it.adapter = listAdapter
        }
    }

    fun search(key: String) {
        viewModel.searchTodo("*${key}*")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //get view model
        viewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return TodoSearchableViewModel(todoRepository) as T
            }

        }).get(TodoSearchableViewModel::class.java)
        //viewmodel observer
        viewModel.searchInfo.observe(this) {
            if (it.size == 0) {
                noresultTextView.visibility = View.VISIBLE
                listAdapter.updateList(listOf())
                listView.visibility = View.INVISIBLE

            } else {
                noresultTextView.visibility = View.INVISIBLE
                listView.visibility = View.VISIBLE
                listAdapter.updateList(it)
            }
        }
        search(arguments?.getString(EXTRA_SEARCH_KEY, "") ?: "")

        //item click callback
        //listAdapter.clickItemEventSubject.skipLast(500, TimeUnit.MILLISECONDS)
        listAdapter.clickItemEventSubject
            .subscribe { _pair ->
            this@TodoSearchableFragment.activity?.also { _activity ->
                when (_pair.second.id) {
                    // todo details
                    R.id.content_card -> {
                        Intent(_activity, TodoDetailActivity::class.java).also { _intent ->
                            _intent.putExtra(DETAIL_ACTIVITY_START_PARAM_TO_DO_INFO, _pair.first)
                            startActivity(_intent)
                        }
                    }
                    R.id.todo_complete_button -> {
                        var  copy = Todo()
                        copyTodo(_pair.first, copy)
                        copy.completed = if(copy.completed) false else true
                        context?.also{
                            todoOpMng.updateTodo(copy)
                        }
                    }
                    R.id.todo_delete_button -> {
                        if (todoOpMng.deleteTodo(_pair.first) == OpResult.TODO_ALREADY_DOING) {
                            Toast.makeText(_activity, "Todo IsEditing", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }.also {
            compositeDisposable.add(it)
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        cancel()
        compositeDisposable.clear()
        super.onDestroyView()
    }
}
