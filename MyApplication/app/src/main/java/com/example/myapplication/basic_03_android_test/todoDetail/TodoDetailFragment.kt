package com.example.myapplication.basic_03_android_test.todoDetail

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.observe
import androidx.navigation.findNavController

import com.example.myapplication.basic_03_android_test.TodoService.TodoLogic
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.model.TodoEditType
import com.example.myapplication.basic_03_android_test.model.TodoEvent
import com.example.myapplication.basic_03_android_test.todoList.TodoViewModel
import com.example.myapplication.basic_03_android_test.uiCommon.CardEvent
import com.example.myapplication.databinding.FragmentTodoDetailBinding
import com.example.myapplication.util.copyTodo
import com.google.android.material.transition.MaterialContainerTransform
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
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
    lateinit var todoDetailViewModel: TodoDetailViewModel

    @Inject
    lateinit var todoViewModel: TodoViewModel

    @Inject
    lateinit var todoLogic: TodoLogic
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
        //todoDetailViewModel = activity?.run{ ViewModelProviders.of(activity as FragmentActivity).get(TodoDetailViewModel::class.java)} ?: throw Exception("no activity")
        //todoViewModel = activity?.run {ViewModelProviders.of(activity as FragmentActivity).get(TodoViewModel::class.java)} ?: throw Exception("no activity")
        todoDetailViewModel.todoDetail.observe(this) {
            setCardView(null, it)
        }
        configCardView()
        compositeDisposable.add(
            completeSubject
                .subscribe() { todoEvent ->
                    when (todoEvent.cardEvent) {
                        CardEvent.COMPLETE -> {
                            val copy = Todo()
                            copyTodo(todoDetailViewModel.todoDetail.value!!, copy)
                            copy.completed = !copy.completed
                            todoLogic.updateTodo(copy)
                        }
                        CardEvent.SAVE_COMMENT -> {
                            val copy = Todo()
                            copyTodo(todoDetailViewModel.todoDetail.value!!, copy)
                            //todo
                            //copy.comment = mCommentEdit.text.toString()
                            todoLogic.updateTodo(copy)
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
        setCardView(null, todoDetailViewModel.todoDetail.value!!)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun configCardView() {
        //complete button, delete button
        View.OnClickListener { _view ->
            val todo = todoDetailViewModel.todoDetail.value
            todo?.let {
                completeSubject.onNext(TodoEvent(CardEvent.COMPLETE, todo, _view))
            }
        }.let {
            binding.todoCard.completeClickListener = it
            binding.todoCard.statusImageClickListener = it
        }

        binding.todoCard.saveClickListener = View.OnClickListener { _view ->
            val todo = todoDetailViewModel.todoDetail.value
            todo?.let {
                completeSubject.onNext(TodoEvent(CardEvent.SAVE_COMMENT, todo, _view))
            }
        }

        //delete button
        binding.todoCard.deleteClickListener = View.OnClickListener { _view ->
            todoViewModel.todoInfo.let {
                if (todoLogic.isTodoEditing(it)) {
                    Toast.makeText(
                        _view.context,
                        "Todo is Editing...",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    todoLogic.deleteTodo(it)
                }
            }
            activity?.finish()
        }

        binding.todoCard.editClickListener = View.OnClickListener { _view ->
            todoViewModel.todoInfo.let {
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
