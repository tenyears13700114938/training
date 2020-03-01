package com.example.myapplication.basic_03_android_test.todoList


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import com.example.myapplication.util.localDateOfTimeFromUtc
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class TodoTimeEditFragment : Fragment() {
    @Inject
    lateinit var todoViewModel: TodoViewModel
    private lateinit var mDatePicker: DatePicker
    private lateinit var mTimePicker: TimePicker
    private val args : TodoTimeEditFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as TodoListActivity).todoListComponent.inject(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_todo_time_edit, container, false)
        mDatePicker = view.findViewById(R.id.datePicker)
        mTimePicker = view.findViewById(R.id.timePicker)

        /*todoViewModel = activity?.run {
            ViewModelProviders.of(this).get(TodoViewModel::class.java)
        } ?: return view*/

        view.findViewById<Button>(R.id.next_button).setOnClickListener {
             SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.JAPAN).apply {
                 this.parse(StringBuilder().append(String.format("%4d",mDatePicker.year))
                    .append("-")
                    .append(String.format("%2d", mDatePicker.month + 1))
                    .append("-")
                    .append(String.format("%2d", mDatePicker.dayOfMonth))
                    .append("T")
                    .append(String.format("%2d", mTimePicker.hour))
                    .append(":")
                    .append(String.format("%2d", mTimePicker.minute))
                     .append(":00")
                    .toString())?.also { _date ->
                     todoViewModel.todoInfo.targetTime = _date.time
                 }
            }
            val photoAction = TodoTimeEditFragmentDirections.actionTodoTimeEditToPhotoEdit(args.editType)
            it.findNavController().navigate(photoAction)
        }
        view.findViewById<Button>(R.id.back_button).setOnClickListener {
            it.findNavController().popBackStack()
        }

        (activity as? NavCommonActivity)?.setTitle("Edit Time")
        setView()
        return view
    }

    //set time display accord todoViewModel
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setView(){
        todoViewModel.todoInfo.targetTime?.takeIf { it != 0L }.let {
            localDateOfTimeFromUtc(todoViewModel.todoInfo.targetTime!!).also { _localDateTime ->
                mDatePicker.updateDate(
                    _localDateTime.year,
                    _localDateTime.monthValue - 1,
                    _localDateTime.dayOfMonth
                )
                mTimePicker.hour = _localDateTime.hour
                mTimePicker.minute = _localDateTime.minute
            }
        }
    }

}
