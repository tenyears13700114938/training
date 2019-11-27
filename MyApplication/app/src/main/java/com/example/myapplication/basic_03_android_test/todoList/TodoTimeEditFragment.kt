package com.example.myapplication.basic_03_android_test.todoList


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs

import com.example.myapplication.R
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class TodoTimeEditFragment : Fragment() {
    private lateinit var todoViewModel: TodoViewModel
    private lateinit var mDatePicker: DatePicker
    private lateinit var mTimePicker: TimePicker
    private val args : TodoTimeEditFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_todo_time_edit, container, false)
        mDatePicker = view.findViewById(R.id.datePicker)
        mTimePicker = view.findViewById(R.id.timePicker)

        todoViewModel = activity?.run {
            ViewModelProviders.of(this).get(TodoViewModel::class.java)
        } ?: return view

        view.findViewById<Button>(R.id.next_button).setOnClickListener {
             SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.JAPAN).apply {
                 var timeString =    StringBuilder().append(String.format("%4d",mDatePicker.year))
                    .append("-")
                    .append(String.format("%2d", mDatePicker.month + 1))
                    .append("-")
                    .append(String.format("%2d", mDatePicker.dayOfMonth))
                    .append("T")
                    .append(String.format("%2d", mTimePicker.hour))
                    .append(":")
                    .append(String.format("%2d", mTimePicker.minute))
                     .append(":00")
                    .toString()
                todoViewModel.todoInfo.targetTime = this.parse(timeString).time

            }
            val photoAction = TodoTimeEditFragmentDirections.actionTodoTimeEditToPhotoEdit(args.editType)
            it.findNavController().navigate(photoAction)
        }
        view.findViewById<Button>(R.id.back_button).setOnClickListener {
            it.findNavController().popBackStack()
        }

        if(activity is TodoListActivity){
            (activity as TodoListActivity).setTitle("Edit Time")
        }
        setView()
        return view
    }

    //set time display accrod todoViewModel
    private fun setView(){

    }

}
