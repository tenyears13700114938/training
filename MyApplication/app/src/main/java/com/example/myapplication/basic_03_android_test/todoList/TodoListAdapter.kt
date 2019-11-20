package com.example.myapplication.basic_03_android_test.todoList

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.model.Todo
import io.reactivex.subjects.PublishSubject
import java.io.File

class TodoListAdapter(val context : Context) : RecyclerView.Adapter<TodoListAdapter.TodoViewHolder>() {
    val todoList : MutableList<Todo> = mutableListOf()
    val clickSubject = PublishSubject.create<Todo>()
    fun updateList(todos : List<Todo>) {
        todoList.clear()
        todoList.addAll(todos)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(LayoutInflater.from(context).inflate(R.layout.todo_item, parent, false))
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(todoList[position], position)
        holder.photoImageView.setOnClickListener { _view ->
            clickSubject.onNext(todoList[position])
        }
    }

    override fun onViewRecycled(holder: TodoViewHolder) {
        super.onViewRecycled(holder)

    }

    inner class TodoViewHolder(_itemView : View) : RecyclerView.ViewHolder(_itemView) {
        val photoImageView : ImageView
        val statusImageView : ImageView
        val titleView : TextView
        val descriptionView : TextView
        val expandButton : ImageView
        val expandButtonArea : ViewGroup
        val completeButton : ImageButton
        val deleteButton : ImageButton
        init {
            photoImageView = itemView.findViewById(R.id.todoItemImage)
            titleView = itemView.findViewById(R.id.todoItemTitle)
            descriptionView = itemView.findViewById(R.id.todoItemDescription)
            statusImageView = itemView.findViewById(R.id.todo_statusIcon)
            expandButton = itemView.findViewById(R.id.expand_button)
            expandButtonArea = itemView.findViewById(R.id.expand_button_area)
            completeButton = itemView.findViewById(R.id.todo_complete_button)
            deleteButton = itemView.findViewById(R.id.todo_delete_button)
        }

        fun bind(item: Todo, position: Int) {
            titleView.text = item.thing
            descriptionView.text = item.description

            Glide.with(context).load(if(TextUtils.isEmpty(item.imageUrl)) R.drawable.saturn_card_view_default else item.imageUrl).into(photoImageView)
            Glide.with(context).load(if(item.completed) R.drawable.ic_check_box_black_24dp else R.drawable.ic_check_box_outline_blank_black_24dp).into(statusImageView)
            expandButton.setOnClickListener {
                expandButton.background = context.getDrawable(if(expandButtonArea.visibility == VISIBLE) R.drawable.ic_arrow_drop_down_black_24dp else R.drawable.ic_arrow_drop_up_black_24dp)
                expandButtonArea.visibility = if(expandButtonArea.visibility == VISIBLE) GONE else VISIBLE
            }
            completeButton.setOnClickListener{
                item.completed = true
                this@TodoListAdapter.notifyItemChanged(position, null)
            }
            deleteButton.setOnClickListener{
                todoList.removeAt(position)
                this@TodoListAdapter.notifyItemRemoved(position)
            }
        }
    }
}