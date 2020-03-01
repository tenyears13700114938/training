package com.example.myapplication.basic_03_android_test.todoList


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import com.example.myapplication.basic_03_android_test.model.TodoEditType
import com.example.myapplication.util.hideSoftInput
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
    private val args : TodoTitleEditFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as TodoListActivity).todoListComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_todo_title_edit, container, false)
        titleEdit = view.findViewById(R.id.todo_title)
        descriptionEdit = view.findViewById(R.id.todo_description)

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
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.decorView?.setOnClickListener {
            val inputManager : InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        }
    }
}
