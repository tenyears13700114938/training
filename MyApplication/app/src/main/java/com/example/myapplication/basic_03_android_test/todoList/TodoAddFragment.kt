package com.example.myapplication.basic_03_android_test.todoList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.R

class TodoAddFragment : Fragment() {
    companion object {
        fun newInstance() : TodoAddFragment{
            return TodoAddFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var fragment = inflater.inflate(R.layout.activity_todo_add, container, false)
        return fragment
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}