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
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey

import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.TodoService.TodoOpMng
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.model.TodoEditType
import com.example.myapplication.basic_03_android_test.todoList.TodoViewModel
import com.example.myapplication.basic_03_android_test.todoRepository.todoRepository
import com.example.myapplication.util.localDateOfTimeFromUtc
import kotlinx.android.synthetic.main.activity_navi_common.*
import kotlinx.coroutines.*
import java.io.File
import java.security.Signature
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
    private  lateinit var todoViewModel : TodoViewModel
    private lateinit var mTitleText : TextView
    private lateinit var mDescriptionText : TextView
    private lateinit var mEditBtn : ImageButton
    private lateinit var mCommentEdit : EditText
    private lateinit var mStatusIconImage : ImageView
    private lateinit var detailsView : View

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
        // Inflate the layout for this fragment
        detailsView = inflater.inflate(R.layout.fragment_todo_detail, container, false)
        todoDetailViewModel = activity?.run{ ViewModelProviders.of(activity as FragmentActivity).get(TodoDetailViewModel::class.java)} ?: throw Exception("no activity")
        todoViewModel = activity?.run {ViewModelProviders.of(activity as FragmentActivity).get(TodoViewModel::class.java)} ?: throw Exception("no activity")
        todoDetailViewModel.todoDetail.observe(this){
            setCardView(detailsView, null, it)
        }
        configCardView(detailsView)
        //setCardView(detailsView, null, todoDetailViewModel.todoDetail.value!!)
        return detailsView
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        (activity as NavCommonActivity).run {
            setTitle("Todo Detail")
        }
        setCardView(detailsView, null, todoDetailViewModel.todoDetail.value!!)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun configCardView(root: View) {
        //complete button, delete button
        root.findViewById<ImageButton>(R.id.todo_complete_button)?.let { _completeBtn ->
            _completeBtn.setOnClickListener { _view ->
                todoDetailViewModel.todoDetail.value?.completed = true;
                Glide.with(activity!!)
                    .load(R.drawable.ic_check_box_black_24dp)
                    .into(mStatusIconImage)
            }
        }
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

        //delete button
        root.findViewById<ImageButton>(R.id.todo_delete_button)?.let { _deleteBtn ->
            _deleteBtn.setOnClickListener { _view ->
                todoViewModel.todoInfo.let {
                    if (TodoOpMng.getIns(_view.context).isTodoEditing(it)) {
                        Toast.makeText(
                            _view.context,
                            "Todo is Editing...",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        TodoOpMng.getIns(_view.context).deleteTodo(it)
                    }
                }
                activity?.finish()
            }
        }
        //comment area
        root.findViewById<View>(R.id.todo_comment_edit_text_area).also {_commentArea ->
            _commentArea.visibility = View.VISIBLE
        }

        mCommentEdit = root.findViewById<EditText>(R.id.todo_comment_edit_text)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setCardView(root : View, oldInfo : Todo?, newInfo : Todo){
        if (oldInfo == null || !Objects.equals(oldInfo.completed, newInfo.completed)) {
            Glide.with(activity!!)
                .load(if (newInfo.completed) R.drawable.ic_check_box_black_24dp else R.drawable.ic_check_box_outline_blank_black_24dp)
                .into(mStatusIconImage)
        }
        //image card size
        if (oldInfo == null || !Objects.equals(oldInfo.imageUrl, newInfo.imageUrl)) {
            root.findViewById<ImageView>(R.id.todoItemImage)?.let { _todoImage ->
                _todoImage.layoutParams.height =
                    _todoImage.context.resources.displayMetrics.widthPixels
                Glide.with(activity!!)
                    .asBitmap()
                    .load(if (TextUtils.isEmpty(newInfo.imageUrl)) R.drawable.saturn_card_view_default else newInfo.imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    //.skipMemoryCache(true)
                    .override(_todoImage.context.resources.displayMetrics.widthPixels,_todoImage.layoutParams.height)
                    .into(object : CustomTarget<Bitmap>(_todoImage.resources.displayMetrics.widthPixels, _todoImage.resources.displayMetrics.widthPixels) {
                        override fun onLoadCleared(placeholder: Drawable?) {
                        }

                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            _todoImage.setImageBitmap(resource)
                           // resource.recycle()
                        }

                        override fun onLoadStarted(placeholder: Drawable?) {
                            super.onLoadStarted(placeholder)
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)
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
            todoViewModel.todoInfo.let {
                if(TodoOpMng.getIns(_editBtn.context).isTodoEditing(it)){
                    Toast.makeText(_editBtn.context, "Todo is Editing", Toast.LENGTH_SHORT).show()
                }
                else {
                    val totitleAction =
                        TodoDetailFragmentDirections.actionTodoDetailFragmentToTodoTitleEditFragment2(TodoEditType.UPDATE)
                    _editBtn.findNavController().navigate(totitleAction)
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
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
