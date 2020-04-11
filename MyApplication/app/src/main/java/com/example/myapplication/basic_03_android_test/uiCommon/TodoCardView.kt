package com.example.myapplication.basic_03_android_test.uiCommon

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.databinding.TodoItemBinding
import com.example.myapplication.util.localDateOfTimeFromUtc
import kotlinx.android.synthetic.main.todo_item.view.*

class TodoCardView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {
    private val TAG = TodoCardView::class.java.simpleName
    private val binding = TodoItemBinding.inflate(LayoutInflater.from(context), this, true)
    private val photoImageView: ImageView
    private val statusImageView: ImageView
    private val titleView: TextView
    private val descriptionView: TextView
    private val expandButton: ImageView
    private val expandButtonArea: ViewGroup
    private val completeButton: ImageButton
    private val deleteButton: ImageButton
    private val dateView: TextView
    private val displayMode: Int
    var completeClickListener = View.OnClickListener {}
    var deleteClickListener = View.OnClickListener {}
    var cardClickListener = View.OnClickListener {}
    var statusImageClickListener = View.OnClickListener {}
    var saveClickListener = View.OnClickListener {}
    var editClickListener = View.OnClickListener {}

    init {
        photoImageView = binding.todoItemImage
        titleView = binding.todoItemTitle
        descriptionView = binding.todoItemDescription
        statusImageView = binding.todoStatusIcon
        expandButton = binding.expandButton
        expandButtonArea = binding.expandButtonArea
        completeButton = binding.todoCompleteButton
        deleteButton = binding.todoDeleteButton
        dateView = binding.todoTargetDate

        context.theme.obtainStyledAttributes(attrs, R.styleable.TodoCardView, 0, 0).apply {
            try {
                displayMode = getInt(R.styleable.TodoCardView_displayMode, 0)
            } finally {
                recycle()
            }
        }

        Log.d(TAG, "displayMode: {$displayMode}")
        configCard(displayMode)

        completeButton.setOnClickListener { completeClickListener.onClick(it) }
        deleteButton.setOnClickListener { deleteClickListener.onClick(it) }
        photoImageView.setOnClickListener { cardClickListener.onClick(it) }
        statusImageView.setOnClickListener { statusImageClickListener.onClick(it) }
        binding.todoEditButton.setOnClickListener{ editClickListener.onClick(it) }
        binding.todoCommentSaveButton.setOnClickListener { saveClickListener.onClick(it) }
    }

    private fun configCard(displayMode : Int){
        if(displayMode == 1){
            titleView.maxLines = 2
            descriptionView.maxLines = 4
            expandButtonArea.visibility = View.VISIBLE
            binding.todoCommentEditTextArea.visibility = View.VISIBLE

            binding.todoCommentEditText.setOnEditorActionListener { v, actionId, event ->
                if ((actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) ||
                    event != null &&
                    event.action == KeyEvent.ACTION_DOWN &&
                    event.keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    binding.todoCommentSaveButton.visibility = View.VISIBLE
                }
                false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(item: Todo) {
        titleView.text = item.thing
        descriptionView.text = item.description
        if (item.targetTime != 0L) {
            dateView.text =
                localDateOfTimeFromUtc(item.targetTime!!).toString().substringBefore("T")
            dateView.visibility = View.VISIBLE
        } else {
            dateView.visibility = View.INVISIBLE
        }

        photoImageView.layoutParams.height =
            if(displayMode == 0) context.resources.displayMetrics.widthPixels * 1 / 2 else context.resources.displayMetrics.widthPixels
        Glide.with(context)
            .asBitmap()
            .load(if (TextUtils.isEmpty(item.imageUrl)) R.drawable.saturn_card_view_default else item.imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .override(
                context.resources.displayMetrics.widthPixels,
                photoImageView.layoutParams.height
            )
            .into(object : CustomTarget<Bitmap>(
                context.resources.displayMetrics.widthPixels,
                photoImageView.layoutParams.height
            ) {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    photoImageView.setImageBitmap(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                }

                override fun onDestroy() {
                    super.onDestroy()
                }
            })

        Glide.with(context)
            .load(if (item.completed) R.drawable.ic_check_box_black_24dp else R.drawable.ic_check_box_outline_blank_black_24dp)
            .into(statusImageView)
    }

    companion object {
        const val NORMAL_MODE = 0
        const val EXPAND_MODE = 1
    }
}

enum class CardEvent {
    COMPLETE,
    DELETE,
    EDIT,
    SAVE_COMMENT,
    STATUS_CHANGE,
    SELECTED
}
