package com.example.myapplication.basic_03_android_test.todoDetail

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.model.TodoEditType
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository
import com.example.myapplication.util.localDateOfTimeFromUtc
import kotlinx.coroutines.*
import java.util.*

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
class TodoDetailFragment : Fragment(), CoroutineScope by MainScope() {
    private var todoParam: Todo? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var todoDetailViewModel: TodoDetailViewModel
    private lateinit var mTitleText : TextView
    private lateinit var mDescriptionText : TextView
    private lateinit var mEditBtn : ImageButton
    private lateinit var mCommentEdit : EditText
    private lateinit var mStatusIconImage : ImageView

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
        todoDetailViewModel = ViewModelProviders.of(this).get(TodoDetailViewModel::class.java)
        //todo observe
        // Inflate the layout for this fragment
        val detailsView = inflater.inflate(R.layout.fragment_todo_detail, container, false)
        configCardView(detailsView)
        setCardView(detailsView, null, todoDetailViewModel.todoDetail.value!!)
        return detailsView
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun configCardView(root: View) {
        //status icon
        mStatusIconImage = root.findViewById(R.id.todo_statusIcon)
        //Title Text max 2 line
        mTitleText = root.findViewById<TextView>(R.id.todoItemTitle)!!.let { _titleText ->
            _titleText.maxLines = 2
            _titleText
        }
        //description Text max 4 Line
        mDescriptionText = root.findViewById<TextView>(R.id.todoItemDescription)!!.let { _descriptionText ->
            _descriptionText.maxLines = 4
            _descriptionText
        }
        //expaned area visible
        root.findViewById<View>(R.id.expand_button_area)?.let { _expandButtonArea ->
            _expandButtonArea.visibility = View.VISIBLE
        }
        //expaned ctl button invisible
        root.findViewById<View>(R.id.expand_button)?.let { _expandBtn ->
            _expandBtn.visibility = View.INVISIBLE
        }
        //edit button visible
        mEditBtn = root.findViewById<ImageButton>(R.id.todo_edit_button)!!.let { _imageEditBtn ->
            _imageEditBtn.visibility = View.VISIBLE
            _imageEditBtn
        }
        //complete button, delete button
        root.findViewById<Button>(R.id.todo_complete_button)?.let { _completeBtn ->
            _completeBtn.setOnClickListener { _view ->
                todoDetailViewModel.todoDetail.value?.completed = true;
                Glide.with(mStatusIconImage)
                    .load(R.drawable.ic_check_box_black_24dp)
                    .into(mStatusIconImage)
            }
        }
        //delete button
        root.findViewById<Button>(R.id.todo_delete_button)?.let { _deleteBtn ->
            _deleteBtn.setOnClickListener { _view ->
                launch {
                    todoDetailViewModel.todoDetail.value?.let {
                        withContext(Dispatchers.IO) {
                            todoRepository.getInstance(_view.context)
                                .deleteToDo(it)
                        }
                    }
                }
            }
        }
        //comment area
        mCommentEdit = root.findViewById<EditText>(R.id.todo_comment_edit_text)!!.let { _todoCommentEdit ->
            _todoCommentEdit.visibility = View.VISIBLE
            _todoCommentEdit
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setCardView(root : View, oldInfo : Todo?, newInfo : Todo){
        if (oldInfo == null || !Objects.equals(oldInfo.completed, newInfo.completed)) {
            Glide.with(mStatusIconImage)
                .load(if (newInfo.completed) R.drawable.ic_check_box_black_24dp else R.drawable.ic_check_box_outline_blank_black_24dp)
                .into(mStatusIconImage)
        }
        //image card size
        if (oldInfo == null || !Objects.equals(oldInfo.imageUrl, newInfo.imageUrl)) {
            root.findViewById<ImageView>(R.id.todoItemImage)?.let { _todoImage ->
                _todoImage.layoutParams.height =
                    _todoImage.context.resources.displayMetrics.widthPixels
                Glide.with(_todoImage)
                    .asBitmap()
                    .load(if (TextUtils.isEmpty(newInfo.imageUrl)) R.drawable.saturn_card_view_default else newInfo.imageUrl)
                    .into(object : CustomTarget<Bitmap>(_todoImage.width, _todoImage.height) {
                        override fun onLoadCleared(placeholder: Drawable?) {
                        }

                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            _todoImage.setImageBitmap(resource)
                        }
                    })
            }
        }
        //Title Text max 2 line
        if (oldInfo == null || !Objects.equals(oldInfo.thing, newInfo.thing)) {
            mTitleText.text = newInfo.thing
        }
        //description Text max 4 Line
        if (oldInfo == null || !Objects.equals(oldInfo.description, newInfo.description)) {
            mDescriptionText.text = newInfo.description
        }
        //date
        if (oldInfo == null || oldInfo.targetTime != newInfo.targetTime) {
            root.findViewById<TextView>(R.id.todo_target_date)?.let { _todoTargetDate ->
                if (newInfo.targetTime != 0L) {
                    _todoTargetDate.text =
                        localDateOfTimeFromUtc(newInfo.targetTime!!).toString()
                            .substringBefore("T")
                    _todoTargetDate.visibility = View.VISIBLE
                } else {
                    _todoTargetDate.visibility = View.INVISIBLE
                }
            }
        }
        //comment
        if (oldInfo == null || !Objects.equals(oldInfo.comment, newInfo.comment)) {
            mCommentEdit.setText(newInfo.comment)
        }
        //edit Button
        mEditBtn.setOnClickListener {_editBtn ->
            val totitleAction =
                TodoDetailFragmentDirections.actionTodoDetailFragmentToTodoTitleEditFragment2(TodoEditType.UPDATE)
            _editBtn.findNavController().navigate(totitleAction)
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
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
