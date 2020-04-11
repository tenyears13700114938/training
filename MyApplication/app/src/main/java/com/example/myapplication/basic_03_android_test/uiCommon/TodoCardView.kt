package com.example.myapplication.basic_03_android_test.uiCommon

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class TodoCardView(context: Context, var mode: Int = NORMAL_MODE) : FrameLayout(context) {
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

    var completeClickListener = View.OnClickListener{}
    var deleteClickListener = View.OnClickListener {}
    var cardClickListener = View.OnClickListener {}
    var statusImageClickListener = View.OnClickListener {}

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

        completeButton.setOnClickListener { completeClickListener.onClick(it) }
        deleteButton.setOnClickListener { deleteClickListener.onClick(it) }
        photoImageView.setOnClickListener{ cardClickListener.onClick(it) }
        statusImageView.setOnClickListener{ statusImageClickListener.onClick(it)}
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
            context.resources.displayMetrics.widthPixels * 1 / 2
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

enum class CardEvent{
    COMPLETE,
    DELETE,
    EDIT,
    STATUS_CHANGE,
    SELECTED
}
