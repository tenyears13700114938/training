package com.example.myapplication.basic_03_android_test.todoDetail

import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.TodoService.OpResult
import com.example.myapplication.basic_03_android_test.TodoService.TodoOpMng
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.todoList.TodoViewModel
import com.example.myapplication.basic_03_android_test.tooBroadcastReceiver.todoBroadcastReceiver
import com.example.myapplication.util.copyTodo
import kotlinx.android.synthetic.main.activity_navi_common.*
import kotlinx.coroutines.*
import java.util.function.Consumer

val DETAIL_ACTIVITY_START_PARAM_TO_DO_INFO = "START_PARAM_TO_DO"
class TodoDetailActivity : NavCommonActivity(), TodoDetailFragment.OnFragmentInteractionListener, CoroutineScope by MainScope(){
    private lateinit var todoDetailViewModel: TodoDetailViewModel
    private lateinit var todoViewModel: TodoViewModel

    override fun onFragmentInteraction(uri: Uri) {
        //todo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.title = "Todo Detail"

        todoDetailViewModel = ViewModelProviders.of(this).get(TodoDetailViewModel::class.java)
        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel::class.java)

        val todoInfo = intent?.getSerializableExtra(
            DETAIL_ACTIVITY_START_PARAM_TO_DO_INFO
        ) ?: return

        todoDetailViewModel.todoDetail.value= todoInfo as Todo
        copyTodo(todoDetailViewModel.todoDetail.value!!, todoViewModel.todoInfo )

        val bundle = Bundle()
        bundle.putSerializable("Todo_Detail", todoInfo)
        val graph = mNavController.navInflater.inflate(R.navigation.todo_detail_navigation)
        mNavController.setGraph(graph, bundle)

        LocalBroadcastManager.getInstance(this).registerReceiver(todoBroadcastReceiver(Consumer{ _intent ->
            if(_intent.getIntExtra(todoBroadcastReceiver.TODO_RESULT_EXTRA_PARAM, 0) == OpResult.UPDATE_OK.result){
                _intent.getSerializableExtra(todoBroadcastReceiver.TODO_INFO_EXTRA_PARAM)?.also {
                    with(it as Todo){
                        todoDetailViewModel.todoDetail.value = this
                    }
                }
            }
        }), IntentFilter(todoBroadcastReceiver.TODO_RESULT_INTENT_FILTER))
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    fun updateTodo(todo: Todo){
        /*launch {
            withContext(Dispatchers.IO) {
                todoRepository.getInstance(this@TodoDetailActivity).updateToDo(todo)
            }
        }*/
        TodoOpMng.getIns(applicationContext).updateTodo(todo)
    }
}
