package com.example.myapplication.basic_03_android_test.todoList

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.util.localDateOfTimeFromUtc
import io.reactivex.subjects.PublishSubject

class TodoListAdapter(val context: Context) :
    RecyclerView.Adapter<TodoListAdapter.TodoViewHolder>() {
    val todoList: MutableList<Todo> = mutableListOf()
    val clickItemEventSubject = PublishSubject.create<Pair<Todo, View>>()
    fun updateList(todos: List<Todo>) {
        todoList.clear()
        todoList.addAll(todos)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.todo_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todoList[position]
        holder.bind(todoList[position], position)
        holder.itemView.setOnClickListener { _view ->
            clickItemEventSubject.onNext(Pair(todo, holder.itemView))
        }
    }

    override fun getItemId(position: Int): Long {
        return todoList[position].id
    }

    override fun onViewRecycled(holder: TodoViewHolder) {
        super.onViewRecycled(holder)

    }

    inner class TodoViewHolder(_itemView: View) : RecyclerView.ViewHolder(_itemView) {
        val photoImageView: ImageView
        val statusImageView: ImageView
        val titleView: TextView
        val descriptionView: TextView
        val expandButton: ImageView
        val expandButtonArea: ViewGroup
        val completeButton: ImageButton
        val deleteButton: ImageButton
        val dateView: TextView

        init {
            photoImageView = itemView.findViewById(R.id.todoItemImage)
            titleView = itemView.findViewById(R.id.todoItemTitle)
            descriptionView = itemView.findViewById(R.id.todoItemDescription)
            statusImageView = itemView.findViewById(R.id.todo_statusIcon)
            expandButton = itemView.findViewById(R.id.expand_button)
            expandButtonArea = itemView.findViewById(R.id.expand_button_area)
            completeButton = itemView.findViewById(R.id.todo_complete_button)
            deleteButton = itemView.findViewById(R.id.todo_delete_button)
            dateView = itemView.findViewById(R.id.todo_target_date)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: Todo, position: Int) {
            titleView.text = item.thing
            descriptionView.text = item.description
            if (item.targetTime != 0L) {
                dateView.text = localDateOfTimeFromUtc(item.targetTime!!).toString().substringBefore("T")
                dateView.visibility = VISIBLE
            } else {
                dateView.visibility = INVISIBLE
            }

            photoImageView.layoutParams.height =
                context.resources.displayMetrics.widthPixels * 1 / 2
            Glide.with(context)
                .asBitmap()
                .load(if (TextUtils.isEmpty(item.imageUrl)) R.drawable.saturn_card_view_default else item.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                //.skipMemoryCache(true)
                .override(context.resources.displayMetrics.widthPixels,photoImageView.layoutParams.height)
                .into(object : CustomTarget<Bitmap>(
                    context.resources.displayMetrics.widthPixels,
                    photoImageView.layoutParams.height
                ) {
                    override fun onLoadCleared(placeholder: Drawable?) {
                       // Glide.with(context).clear(photoImageView)
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
            expandButton.setOnClickListener {
                expandButton.background =
                    context.getDrawable(if (expandButtonArea.visibility == VISIBLE) R.drawable.ic_arrow_drop_down_black_24dp else R.drawable.ic_arrow_drop_up_black_24dp)
                expandButtonArea.visibility =
                    if (expandButtonArea.visibility == VISIBLE) GONE else VISIBLE
            }
            completeButton.setOnClickListener {_view ->
                todoList.indexOf(item).takeIf { index -> index != -1 }?.also {
                    clickItemEventSubject.onNext(Pair(item, _view))
                }
            }
            deleteButton.setOnClickListener {_view ->
                todoList.indexOf(item).takeIf { index -> index != -1 }?.also {
                    todoList.removeAt(it)
                    clickItemEventSubject.onNext(Pair(item, _view))
                }
            }
        }
    }
}