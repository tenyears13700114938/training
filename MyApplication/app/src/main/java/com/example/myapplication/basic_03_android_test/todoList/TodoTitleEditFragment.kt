package com.example.myapplication.basic_03_android_test.todoList


import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.basic_03_android_test.Flux.CoroutineDispatcher
import com.example.myapplication.basic_03_android_test.Flux.TodoActionCreator
import com.example.myapplication.basic_03_android_test.Flux.TodoStore
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.model.TodoEditType
import com.example.myapplication.basic_03_android_test.model.TodoPriority
import com.example.myapplication.databinding.FragmentTodoTitleEditBinding
import com.example.myapplication.util.hideSoftInput
import com.google.android.material.transition.MaterialContainerTransform
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.nav_buttons.view.*
import java.io.File
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class TodoTitleEditFragment : DaggerFragment() {
    lateinit var binding: FragmentTodoTitleEditBinding

    @Inject
    lateinit var todoStore: TodoStore

    @Inject
    lateinit var dispatcher: CoroutineDispatcher

    @Inject
    lateinit var actionCreator: TodoActionCreator

    private val args: TodoTitleEditFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /*(activity as TodoListActivity).todoListComponent.inject(this)*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform(requireContext()).apply {
            fadeMode = MaterialContainerTransform.FADE_MODE_OUT
        }
        sharedElementReturnTransition = MaterialContainerTransform(requireContext()).apply {
            fadeMode = MaterialContainerTransform.FADE_MODE_OUT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTodoTitleEditBinding.inflate(inflater, container, false)

        binding.navButtons.next_button.setOnClickListener {
            (todoStore.editingTodo.value?.copy() ?: Todo()).apply {
                thing = binding.todoTitle.text?.toString() ?: ""
                description = binding.todoDescription.text?.toString() ?: ""
                priority = binding.todoPriority.selectedItem as String
                dispatcher.dispatch(actionCreator.editedTodo(this))
            }
            activity?.let {
                hideSoftInput(it)
            }
            val timeAction =
                TodoTitleEditFragmentDirections.actionTodoTitleEditToTimeEdit(args.editType)
            it.findNavController().navigate(timeAction)
        }

        binding.navButtons.back_button.setOnClickListener {
            it.findNavController().popBackStack()
            if (args.editType == TodoEditType.CREATE) {
                todoStore.editingTodo.value?.imageUrl?.also { _imageUrl ->
                    File(_imageUrl).delete()
                }
                dispatcher.dispatch(actionCreator.editedTodo(Todo()))
            }
        }

        if (activity is NavCommonActivity) {
            (activity as NavCommonActivity).apply {
                setMenuVisibility(false)
                setTitle("Edit Title and description")
            }
        }
        setView()
        return binding.root;
    }

    private fun setView() {
        binding.todoTitle.setText(todoStore.editingTodo.value?.thing)
        binding.todoDescription.setText(todoStore.editingTodo.value?.description)
        val priorityList = mutableListOf<String>().apply {
            add(TodoPriority.EMERGENCY.name)
            add(TodoPriority.HIGH.name)
            add(TodoPriority.MIDDLE.name)
            add(TodoPriority.LOW.name)
        }
        binding.todoPriority.adapter = ArrayAdapter<String>(
            this.requireContext(),
            android.R.layout.simple_spinner_item,
            priorityList
        )
        /*binding.todoPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
            }
        }*/
        binding.todoPriority.setSelection(
            todoStore.editingTodo.value?.priority.takeIf { !TextUtils.isEmpty(it) }?.let {
                priorityList.indexOf(it)
            } ?: 0
        )
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.decorView?.setOnClickListener {
            val inputManager: InputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        }
    }
}
