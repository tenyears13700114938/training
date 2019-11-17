package com.example.myapplication.basic_03_android_test.todoList


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController

import com.example.myapplication.R

/**
 * A simple [Fragment] subclass.
 */
class TodoTimeEditFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_todo_time_edit, container, false)
        view.findViewById<Button>(R.id.next_button).setOnClickListener {
            it.findNavController().navigate(R.id.todoPhotoEditFragment, null)
        }
        view.findViewById<Button>(R.id.back_button).setOnClickListener {
            it.findNavController().popBackStack()
        }

        if(activity is TodoListActivity){
            (activity as TodoListActivity).setTitle("Edit Time")
        }
        return view
    }


}
