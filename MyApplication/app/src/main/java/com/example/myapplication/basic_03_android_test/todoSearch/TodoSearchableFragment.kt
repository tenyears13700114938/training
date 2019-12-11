package com.example.myapplication.basic_03_android_test.todoSearch

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import androidx.paging.LivePagedListBuilder
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        search(arguments?.getString(EXTRA_SEARCH_KEY, "") ?: "")
        return inflater.inflate(R.layout.todo_searchable_fragment, container, false)
    }

    fun search(key: String) {
        launch {
           // withContext(Dispatchers.IO) {
                todoRepository.getInstance(this@TodoSearchableFragment.requireContext())
                    .searchTodo("*${key}*").also { _searchDataFactory ->
                    viewModel.searchInfo = LivePagedListBuilder(_searchDataFactory, 2).build()
                       viewModel.searchInfo.observe(this@TodoSearchableFragment){
                           it
                       }
                }
           // }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TodoSearchableViewModel::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cancel()
    }
}
