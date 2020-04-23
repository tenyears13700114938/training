package com.example.myapplication.basic_03_android_test.todoList


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.basic_03_android_test.Flux.CoroutineDispatcher
import com.example.myapplication.basic_03_android_test.Flux.TodoActionCreator
import com.example.myapplication.basic_03_android_test.Flux.TodoEditingStore
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.databinding.FragmentTodoTimeEditBinding
import com.example.myapplication.util.localDateOfTimeFromUtc
import com.google.android.material.transition.MaterialSharedAxis
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.nav_buttons.view.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class TodoTimeEditFragment : DaggerFragment() {
    @Inject
    lateinit var dispatcher: CoroutineDispatcher

    @Inject
    lateinit var todoStore: TodoEditingStore

    @Inject
    lateinit var actionCreator: TodoActionCreator
    lateinit var binding: FragmentTodoTimeEditBinding

    private val args: TodoTimeEditFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /*(activity as TodoListActivity).todoListComponent.inject(this)*/
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTodoTimeEditBinding.inflate(inflater, container, false)

        binding.navButtons.next_button.setOnClickListener {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.JAPAN).apply {
                this.parse(
                    StringBuilder().append(String.format("%4d", binding.datePicker.year))
                        .append("-")
                        .append(String.format("%2d", binding.datePicker.month + 1))
                        .append("-")
                        .append(String.format("%2d", binding.datePicker.dayOfMonth))
                        .append("T")
                        .append(String.format("%2d", binding.timePicker.hour))
                        .append(":")
                        .append(String.format("%2d", binding.timePicker.minute))
                        .append(":00")
                        .toString()
                )?.let { _date ->
                    (todoStore.editingTodo.value?.copy() ?: Todo()).apply {
                        targetTime = _date.time
                        dispatcher.dispatch(actionCreator.editedTodo(this))
                    }
                }
            }
            val photoAction =
                TodoTimeEditFragmentDirections.actionTodoTimeEditToPhotoEdit(args.editType)
            it.findNavController().navigate(photoAction)
        }
        binding.navButtons.back_button.setOnClickListener {
            it.findNavController().popBackStack()
        }

        (activity as? NavCommonActivity)?.setTitle("Edit Time")
        setView()
        return binding.root
    }

    //set time display accord todoViewModel
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setView() {
        todoStore.editingTodo.value?.targetTime?.takeIf { it != 0L }?.let {
            localDateOfTimeFromUtc(it).also { _localDateTime ->
                binding.datePicker.updateDate(
                    _localDateTime.year,
                    _localDateTime.monthValue - 1,
                    _localDateTime.dayOfMonth
                )
                binding.timePicker.hour = _localDateTime.hour
                binding.timePicker.minute = _localDateTime.minute
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val forward =
            MaterialSharedAxis.create(requireContext(), MaterialSharedAxis.X, true).apply {
                secondaryTransition = null
                duration = 100
            }
        enterTransition = forward

        val backward =
            MaterialSharedAxis.create(requireContext(), MaterialSharedAxis.X, false).apply {
                secondaryTransition = null
                duration = 100
            }
        exitTransition = backward
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}
