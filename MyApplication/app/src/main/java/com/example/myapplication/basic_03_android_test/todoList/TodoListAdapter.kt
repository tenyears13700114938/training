package com.example.myapplication.basic_03_android_test.todoList

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.model.Todo
import io.reactivex.subjects.PublishSubject
import java.io.File
import java.util.function.ToDoubleBiFunction

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
        holder.bind(todoList[position])
        holder.imageView.setOnClickListener { _view ->
            clickSubject.onNext(todoList[position])
        }
    }

    override fun onViewRecycled(holder: TodoViewHolder) {
        super.onViewRecycled(holder)

    }

    inner class TodoViewHolder(_itemView : View) : RecyclerView.ViewHolder(_itemView) {
        val imageView : ImageView
        val titleView : TextView
        val descriptionView : TextView
        init {
            imageView = itemView.findViewById(R.id.todoItemImage)
            titleView = itemView.findViewById(R.id.todoItemTitle)
            descriptionView = itemView.findViewById(R.id.todoItemDescription)
        }

        fun bind(item: Todo) {
            titleView.text = item.thing
            descriptionView.text = item.description
            if (TextUtils.isEmpty(item.imageUrl)) {
                Glide.with(context).load(R.drawable.saturn_card_view_default).into(imageView)
            } else {
                Glide.with(context).load(File(item.imageUrl)).into(imageView)
            }
            //imageView.background = itemView.context.resources.getDrawable(if(item.completed) R.drawable.ic_check_box_black_24dp else R.drawable.ic_check_box_outline_blank_black_24dp)
        }
    }
}