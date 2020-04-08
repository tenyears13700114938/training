package com.example.myapplication.basic_03_android_test.todoList


import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import com.example.myapplication.basic_03_android_test.model.TodoEditType
import com.example.myapplication.basic_03_android_test.model.TodoPriority
import com.example.myapplication.util.hideSoftInput
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import java.io.File
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class TodoTitleEditFragment : Fragment() {
    @Inject
    lateinit var todoViewModel: TodoViewModel
    private lateinit var titleEdit : EditText
    private lateinit var descriptionEdit : EditText
    private lateinit var prioritySpinner : Spinner
    private val args : TodoTitleEditFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as TodoListActivity).todoListComponent.inject(this)
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
        val view = inflater.inflate(R.layout.fragment_todo_title_edit, container, false)
        titleEdit = view.findViewById(R.id.todo_title)
        descriptionEdit = view.findViewById(R.id.todo_description)
        prioritySpinner = view.findViewById(R.id.todo_priority)
        /*todoViewModel = activity?.run{
            ViewModelProviders.of(this).get(TodoViewModel::class.java)
        } ?: return view*/

        view.findViewById<Button>(R.id.next_button).setOnClickListener{
            todoViewModel.todoInfo.thing = titleEdit.text?.toString() ?: ""
            todoViewModel.todoInfo.description = descriptionEdit.text?.toString() ?: ""

            activity?.let {
                hideSoftInput(it)
            }
            val timeAction = TodoTitleEditFragmentDirections.actionTodoTitleEditToTimeEdit(args.editType)
            it.findNavController().navigate(timeAction)
        }

        view.findViewById<Button>(R.id.back_button).setOnClickListener {
            it.findNavController().popBackStack()
            if(args.editType == TodoEditType.CREATE) {
                todoViewModel.todoInfo.imageUrl?.also { _imageUrl ->
                    File(_imageUrl).delete()
                }
                todoViewModel.todoInfo.reset()
            }
        }

        if(activity is NavCommonActivity){
            (activity as NavCommonActivity).apply {
                setMenuVisibility(false)
                setTitle("Edit Title and description")
            }
        }
        setView()
        return view;
    }

    private fun setView() {
        titleEdit.setText(todoViewModel.todoInfo.thing)
        descriptionEdit.setText(todoViewModel.todoInfo.description)
        val priorityList = mutableListOf<String>().apply {
            add(TodoPriority.EMERGENCY.name)
            add(TodoPriority.HIGH.name)
            add(TodoPriority.MIDDLE.name)
            add(TodoPriority.LOW.name)
        }
        prioritySpinner.adapter = ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_spinner_item, priorityList)
        prioritySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                todoViewModel.todoInfo.priority = parent?.getItemAtPosition(position) as String
            }
        }
        prioritySpinner.setSelection(
            if (TextUtils.isEmpty(todoViewModel.todoInfo.priority)) 0 else priorityList.indexOf(
                todoViewModel.todoInfo.priority
            )
        )
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.decorView?.setOnClickListener {
            val inputManager : InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        }
    }
}
