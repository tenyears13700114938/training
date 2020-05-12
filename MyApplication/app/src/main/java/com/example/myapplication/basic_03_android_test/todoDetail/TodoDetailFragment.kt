package com.example.myapplication.basic_03_android_test.todoDetail

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import com.example.myapplication.basic_03_android_test.Flux.CoroutineDispatcher
import com.example.myapplication.basic_03_android_test.Flux.TodoActionCreator
import com.example.myapplication.basic_03_android_test.Flux.TodoDetailStore
import com.example.myapplication.basic_03_android_test.TodoService.ITodoLogic
import com.example.myapplication.basic_03_android_test.TodoService.TodoLogic
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.model.TodoEditType
import com.example.myapplication.basic_03_android_test.model.TodoEvent
import com.example.myapplication.basic_03_android_test.uiCommon.CardEvent
import com.example.myapplication.databinding.FragmentTodoDetailBinding
import com.google.android.material.transition.MaterialContainerTransform
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.todo_item.view.*
import kotlinx.coroutines.*
import javax.inject.Inject

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_TODO_DETAIL = "Todo_Detail"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TodoDetailFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [TodoDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TodoDetailFragment : DaggerFragment(), CoroutineScope by MainScope() {
    private var todoParam: Todo? = null
    private var listener: OnFragmentInteractionListener? = null

    @Inject
    lateinit var dispatcher: CoroutineDispatcher

    @Inject
    lateinit var store: TodoDetailStore

    @Inject
    lateinit var todoActionCreator: TodoActionCreator

    @Inject
    lateinit var todoLogic: ITodoLogic
    private lateinit var binding: FragmentTodoDetailBinding
    private val completeSubject = PublishSubject.create<TodoEvent>()
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            todoParam = it.getSerializable(ARG_TODO_DETAIL) as Todo
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTodoDetailBinding.inflate(LayoutInflater.from(context), container, false)
        //binding.todoCard.detailsView.transitionName = "shared_todo_card"
        configCardView()
        compositeDisposable.add(
            completeSubject
                .subscribe() { todoEvent ->
                    when (todoEvent.cardEvent) {
                        CardEvent.COMPLETE -> {
                            val copy = store.editingTodo.value!!.copy()
                            copy.completed = !copy.completed
                            dispatcher.dispatch(todoActionCreator.editedTodo(copy))
                            launch {
                                withContext(Dispatchers.Default) {
                                    todoLogic.updateTodo(copy)
                                }
                            }
                        }
                        CardEvent.SAVE_COMMENT -> {
                            val copy =
                                store.editingTodo.value!!.copy(comment = binding.todoCard.todo_comment_edit_text.text.toString())
                            dispatcher.dispatch(todoActionCreator.editedTodo(copy))
                            launch {
                                withContext(Dispatchers.Default) {
                                    todoLogic.updateTodo(copy)
                                }
                            }
                        }
                    }
                }
        )
        return binding.root
    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        super.onDestroyView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            it.window.sharedElementEnterTransition = MaterialContainerTransform(it).apply {
                addTarget(binding.root)
                duration = 200L
            }
            it.window.sharedElementReturnTransition = MaterialContainerTransform(it).apply {
                addTarget(binding.root)
                duration = 200L
            }
            it.startPostponedEnterTransition()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        (activity as NavCommonActivity).run {
            setTitle("Todo Detail")
        }
        store.editingTodo.observe(this) {
            setCardView(null, it)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun configCardView() {
        //complete button, delete button
        View.OnClickListener { _view ->
            store.editingTodo.value?.let {
                completeSubject.onNext(TodoEvent(CardEvent.COMPLETE, it, _view))
            }
        }.let {
            binding.todoCard.completeClickListener = it
            binding.todoCard.statusImageClickListener = it
        }

        binding.todoCard.saveClickListener = View.OnClickListener { _view ->
            store.editingTodo.value?.let {
                completeSubject.onNext(TodoEvent(CardEvent.SAVE_COMMENT, it, _view))
            }
        }

        //delete button
        binding.todoCard.deleteClickListener = View.OnClickListener { _view ->
            store.editingTodo.value?.let {
                if (todoLogic.isTodoEditing(it)) {
                    Toast.makeText(
                        _view.context,
                        "Todo is Editing...",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    launch {
                        withContext(Dispatchers.Default) {
                            todoLogic.deleteTodo(it)
                        }
                    }
                }
            }
            activity?.finish()
        }

        binding.todoCard.editClickListener = View.OnClickListener { _view ->
            store.editingTodo.value?.let {
                if (todoLogic.isTodoEditing(it)) {
                    Toast.makeText(_view.context, "Todo is Editing", Toast.LENGTH_SHORT).show()
                } else {
                    val totitleAction =
                        TodoDetailFragmentDirections.actionTodoDetailFragmentToTodoTitleEditFragment2(
                            TodoEditType.UPDATE
                        )
                    _view.findNavController().navigate(totitleAction)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setCardView(oldInfo: Todo?, newInfo: Todo) {
        binding.todoCard.bind(newInfo)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /*(activity as TodoDetailActivity).todoDetailComponent.inject(this)*/

        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }
}
