package com.example.myapplication.basic_03_android_test.todoSearch

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoList.TodoListAdapter
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository
import io.reactivex.exceptions.Exceptions
import kotlinx.coroutines.*
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

    private lateinit var viewModel: TodoSearchableViewModel
    private lateinit var listView : RecyclerView
    private lateinit var listAdapter : TodoListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.todo_searchable_fragment, container, false)
        initListView(rootView)
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
        viewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return TodoSearchableViewModel(todoRepository.getInstance(this@TodoSearchableFragment.requireContext())) as T
            }

        }).get(TodoSearchableViewModel::class.java)

        viewModel.searchInfo.observe(this){
            listAdapter.updateList(it)
        }

        search(arguments?.getString(EXTRA_SEARCH_KEY, "") ?: "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cancel()
    }
}
