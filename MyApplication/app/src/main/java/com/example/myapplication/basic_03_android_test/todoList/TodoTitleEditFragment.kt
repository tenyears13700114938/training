package com.example.myapplication.basic_03_android_test.todoList


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

import com.example.myapplication.R

/**
 * A simple [Fragment] subclass.
 */
class TodoTitleEditFragment : Fragment() {
    private lateinit var todoViewModel: TodoViewModel
    private lateinit var titleEdit : EditText
    private lateinit var descriptionEdit : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_todo_title_edit, container, false)
        titleEdit = view.findViewById(R.id.todo_title)
        descriptionEdit = view.findViewById(R.id.todo_description)

        todoViewModel = activity?.run{
            ViewModelProviders.of(this).get(TodoViewModel::class.java)
        } ?: return view

        view.findViewById<Button>(R.id.next_button).setOnClickListener{
            todoViewModel.todoInfo.thing = titleEdit.text?.toString() ?: ""
            todoViewModel.todoInfo.description = descriptionEdit.text?.toString() ?: ""

            it.findNavController().navigate(R.id.todoTimeEditFragment)
        }

        view.findViewById<Button>(R.id.back_button).setOnClickListener {
            it.findNavController().popBackStack()
        }

        if(activity is TodoListActivity){
            (activity as TodoListActivity).apply {
                setMenuVisibility(false)
                setTitle("Edit Title and description")
            }
        }
        return view;
    }


}
