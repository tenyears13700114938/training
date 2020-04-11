package com.example.myapplication.basic_03_android_test.todoList

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.model.TodoEvent
import com.example.myapplication.basic_03_android_test.model.TodoPriority
import com.example.myapplication.basic_03_android_test.uiCommon.CardEvent
import com.example.myapplication.basic_03_android_test.uiCommon.TodoCardView
import io.reactivex.subjects.PublishSubject
import java.util.*

class TodoListAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), TodoListItemDecoration.StickyHeaderInterface {
    val todoList: MutableList<Any> = mutableListOf()
    val clickItemEventSubject = PublishSubject.create<TodoEvent>()
    fun updateList(todos: List<Todo>) {
        todoList.clear()
        todoList.addAll(todos.sortedWith(object : Comparator<Todo> {
            override fun compare(o1: Todo, o2: Todo): Int {
                val priority1 = TodoPriority.valueOf(o1.priority ?: TodoPriority.LOW.name)
                val priority2 = TodoPriority.valueOf(o2.priority ?: TodoPriority.LOW.name)
                return (priority1.ordinal - priority2.ordinal)
            }
        }))
        val itemInterator = todoList.listIterator()
        var curHeader: TodoPriority? = null
        for (item in itemInterator) {
            if (item is Todo) {
                val priority1 = TodoPriority.valueOf(item.priority ?: TodoPriority.LOW.name)
                if (!Objects.equals(curHeader, priority1)) {
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
        return if (isHeader(position)) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                TodoCardView(context).let {
                    it.layoutParams = RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    TodoViewHolder(it)
                }
            }
            else -> {
                TodoHeaderViewHolder(
                    LayoutInflater.from(context).inflate(
                        R.layout.todo_priority_item,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val todo = todoList[position]
        when (todo) {
            is Todo -> {
                if (holder is TodoViewHolder) {
                    holder.bind(todo, position)
                }
            }
            is TodoPriority -> {
                if (holder is TodoHeaderViewHolder) {
                    holder.bind(todo)
                }
            }
        }
    }

    override fun getItemId(position: Int): Long {
        val todoItem = todoList[position]
        return when (todoItem) {
            is Todo -> todoItem.id
            is TodoPriority -> -position.toLong()
            else -> -position.toLong()
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)

    }

    inner class TodoHeaderViewHolder(_itemView: View) : RecyclerView.ViewHolder(_itemView) {
        val priorityHeader: TextView = _itemView.findViewById(R.id.priority_group_header)

        fun bind(item: TodoPriority) {
            priorityHeader.text = "Priority: ${item.name}"
        }
    }

    inner class TodoViewHolder(_itemView: TodoCardView) : RecyclerView.ViewHolder(_itemView) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: Todo, position: Int) = (itemView as TodoCardView).apply {
            bind(item)
            statusImageClickListener = View.OnClickListener { _view ->
                clickItemEventSubject.onNext(TodoEvent(CardEvent.STATUS_CHANGE, item, _view))
            }
            cardClickListener = View.OnClickListener { _view ->
                clickItemEventSubject.onNext(TodoEvent(CardEvent.SELECTED, item, _view))
            }
        }
    }

    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        for (position in itemPosition downTo 0) {
            if (todoList[position] is TodoPriority) {
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
            if (header is TodoPriority) {
                it.text = "Priority: ${header.name}"
            }
        }
    }

    override fun isHeader(itemPosition: Int): Boolean {
        return todoList[itemPosition] !is Todo
    }
}
