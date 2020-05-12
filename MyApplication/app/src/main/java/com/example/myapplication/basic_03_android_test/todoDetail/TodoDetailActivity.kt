package com.example.myapplication.basic_03_android_test.todoDetail

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.observe
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.Flux.CoroutineDispatcher
import com.example.myapplication.basic_03_android_test.Flux.TodoActionCreator
import com.example.myapplication.basic_03_android_test.Flux.TodoDetailStore
import com.example.myapplication.basic_03_android_test.TodoService.ITodoLogic
import com.example.myapplication.basic_03_android_test.TodoService.TodoLogic
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.tooBroadcastReceiver.todoBroadcastReceiver
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
import javax.inject.Inject

val DETAIL_ACTIVITY_START_PARAM_TO_DO_INFO = "START_PARAM_TO_DO"

class TodoDetailActivity : NavCommonActivity(), HasAndroidInjector,
    TodoDetailFragment.OnFragmentInteractionListener, CoroutineScope by MainScope() {
    @Inject
    lateinit var todoDetailStore: TodoDetailStore

    @Inject
    lateinit var actionCreator: TodoActionCreator

    @Inject
    lateinit var dispatcher: CoroutineDispatcher

    @Inject
    lateinit var todoLogic: ITodoLogic

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    private lateinit var broadcastReceiver: todoBroadcastReceiver
    override fun onFragmentInteraction(uri: Uri) {
        //todo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        /*todoDetailComponent = (application as MyApplication).appComponent.registrationTodoDetailComponent().create(this)
        todoDetailComponent.inject(this)*/
        AndroidInjection.inject(this)

        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        postponeEnterTransition()

        super.onCreate(savedInstanceState)

        toolbar.title = "Todo Detail"

        val todoInfo = intent?.getSerializableExtra(
            DETAIL_ACTIVITY_START_PARAM_TO_DO_INFO
        ) ?: return
        val bundle = Bundle()
        bundle.putSerializable("Todo_Detail", todoInfo)
        val graph = mNavController.navInflater.inflate(R.navigation.todo_detail_navigation)
        mNavController.setGraph(graph, bundle)

        /*broadcastReceiver = todoBroadcastReceiver(Consumer{ _intent ->
     if(_intent.getIntExtra(todoBroadcastReceiver.TODO_RESULT_EXTRA_PARAM, 0) == OpResult.UPDATE_OK.result){
         _intent.getSerializableExtra(todoBroadcastReceiver.TODO_INFO_EXTRA_PARAM)?.also {
             with(it as Todo){
                 todoDetailViewModel.todoDetail.value = this
             }
         }
     }
 })
 LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter(todoBroadcastReceiver.TODO_RESULT_INTENT_FILTER))*/
    }

    override fun onResume() {
        super.onResume()
        val todoInfo = intent?.getSerializableExtra(
            DETAIL_ACTIVITY_START_PARAM_TO_DO_INFO
        ) ?: return
        todoDetailStore.editingTodo.observe(this) {}
        dispatcher.dispatch(actionCreator.detailLoaded((todoInfo as Todo).copy()))
    }

    override fun onDestroy() {
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        dispatcher.dispatch(actionCreator.editedTodo(Todo()))
        super.onDestroy()
        cancel()
    }

    fun updateTodo(todo: Todo) {
        launch {
            todoLogic.updateTodo(todo)
        }
    }

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector
}
