package com.example.myapplication.basic_03_android_test.addTodo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.model.Todo

import kotlinx.android.synthetic.main.activity_todo_add.*

class TodoAddActivity : AppCompatActivity() {
    private lateinit var mTitleEditView : EditText
    private lateinit var mDescriptionEditView : EditText

    companion object {
        val Request_Code_Add_ToDo = 100
        val ToDo_Extra_Parameter = "ToDoExtraParameter"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_add)
        setSupportActionBar(toolbar)

        mTitleEditView = findViewById(R.id.titleEditView)
        mDescriptionEditView = findViewById(R.id.descriptionEditText)

        fab.setOnClickListener { view ->
            val title = mTitleEditView.text
            val description = mDescriptionEditView.text
            Todo(0, title.toString(), false, description.toString()).let {_todo ->
                Intent().also { _intent ->
                    _intent.putExtra(ToDo_Extra_Parameter, _todo)
                    setResult(Activity.RESULT_OK, _intent)
                    finish()
                }
            }
        }
    }

}
