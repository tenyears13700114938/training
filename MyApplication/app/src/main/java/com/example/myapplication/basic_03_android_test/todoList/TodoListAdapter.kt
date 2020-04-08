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
import com.example.myapplication.basic_03_android_test.model.TodoPriority
import com.example.myapplication.util.localDateOfTimeFromUtc
import io.reactivex.subjects.PublishSubject
import java.util.*
import kotlin.Comparator

class TodoListAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), TodoListItemDecoration.StickyHeaderInterface {
    val todoList: MutableList<Any> = mutableListOf()
    val clickItemEventSubject = PublishSubject.create<Pair<Todo, View>>()
    fun updateList(todos: List<Todo>) {
        todoList.clear()
        todoList.addAll(todos.sortedWith(object: Comparator<Todo>{
            override fun compare(o1: Todo, o2: Todo): Int {
                val priority1 = TodoPriority.valueOf(o1.priority ?: TodoPriority.LOW.name)
                val priority2 = TodoPriority.valueOf(o2.priority ?: TodoPriority.LOW.name)
                return (priority1.ordinal - priority2.ordinal)
            }
        }))
        val itemInterator = todoList.listIterator()
        var curHeader : TodoPriority? = null
        for(item in itemInterator){
            if(item is Todo) {
                val priority1 = TodoPriority.valueOf(item.priority ?: TodoPriority.LOW.name)
                if(!Objects.equals(curHeader, priority1)){
                    itemInterator.previous()
                    itemInterator.add(priority1)
                    curHeader = priority1
                    itemInterator.next()
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if(isHeader(position)) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) TodoViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.todo_item,
                parent,
                false
            )
        ) else TodoHeaderViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.todo_priority_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val todo = todoList[position]
        when(todo){
            is Todo -> {
                if(holder is TodoViewHolder) {
                    holder.bind(todo, position)
                }
                holder.itemView.setOnClickListener { _view ->
                    clickItemEventSubject.onNext(Pair(todo, holder.itemView))
                }
            }
            is TodoPriority -> {
                if(holder is TodoHeaderViewHolder){
                    holder.bind(todo)
                }
            }
        }
    }

    override fun getItemId(position: Int): Long {
        val todoItem = todoList[position]
        return when(todoItem){
            is Todo -> todoItem.id
            is TodoPriority -> -position.toLong()
            else -> -position.toLong()
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)

    }

    inner class TodoHeaderViewHolder(_itemView: View) : RecyclerView.ViewHolder(_itemView){
        val priorityHeader : TextView

        init {
            priorityHeader = _itemView.findViewById(R.id.priority_group_header)
        }

        fun bind(item : TodoPriority){
            priorityHeader.text = "Priority: ${item.name}"
        }
    }

    inner class TodoViewHolder(_itemView: View) : RecyclerView.ViewHolder(_itemView) {
        val photoImageView: ImageView
        val statusImageView: ImageView
        val titleView: TextView
        val descriptionView: TextView
        val expandButton: ImageView
        //val expandButtonArea: ViewGroup
        val completeButton: ImageButton
        val deleteButton: ImageButton
        val dateView: TextView

        init {
            photoImageView = itemView.findViewById(R.id.todoItemImage)
            titleView = itemView.findViewById(R.id.todoItemTitle)
            descriptionView = itemView.findViewById(R.id.todoItemDescription)
            statusImageView = itemView.findViewById(R.id.todo_statusIcon)
            expandButton = itemView.findViewById(R.id.expand_button)
            //expandButtonArea = itemView.findViewById(R.id.expand_button_area)
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
            /*Glide.with(context)
                .asBitmap()
                .load(if (TextUtils.isEmpty(item.imageUrl)) R.drawable.saturn_card_view_default else item.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .override(context.resources.displayMetrics.widthPixels,photoImageView.layoutParams.height)
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
                .into(statusImageView)*/
            /*expandButton.setOnClickListener {
                expandButton.background =
                    context.getDrawable(if (expandButtonArea.visibility == VISIBLE) R.drawable.ic_arrow_drop_down_black_24dp else R.drawable.ic_arrow_drop_up_black_24dp)
                expandButtonArea.visibility =
                    if (expandButtonArea.visibility == VISIBLE) GONE else VISIBLE
            }*/
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

    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        for(position in itemPosition downTo 0){
            if(todoList[position] is TodoPriority){
                return position
            }
        }
        return -1
    }

    override fun getHeaderLayout(headerPosition: Int): Int {
        return R.layout.todo_priority_item
    }

    override fun bindHeaderData(header: View, headerPosition: Int) {
        header.findViewById<TextView>(R.id.priority_group_header).also {
            val header = todoList[headerPosition]
            if(header is TodoPriority){
                it.text = "Priority: ${header.name}"
            }
        }
    }

    override fun isHeader(itemPosition: Int): Boolean {
        return if(todoList[itemPosition] is Todo) false else true
    }
}