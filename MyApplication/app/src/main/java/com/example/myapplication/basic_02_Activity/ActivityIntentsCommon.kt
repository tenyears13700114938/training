package com.example.myapplication.basic_02_Activity

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.BaseActivity
import com.example.myapplication.R

import kotlinx.android.synthetic.main.activity_intents_common.*
import kotlinx.android.synthetic.main.content_activity_intents_common.*

abstract class ActivityIntentsCommon : BaseActivity() {
    lateinit var mActionButton : Button
    lateinit var mMessageTextView: TextView
    lateinit var mEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intents_common)
        setSupportActionBar(toolbar)

        //instance view
        mActionButton = findViewById(R.id.messageActionBtn)
        mMessageTextView = findViewById(R.id.messageTextView)
        mEditText = findViewById(R.id.messageEditText)

        mActionButton.let{
            it.text = actionName()
            it.setOnClickListener(actionHandler())
        }
        Log.d(TAG(),"onCreate")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG(), "onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG(),"onRestoreInstanceState")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG(), "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG(), "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG(), "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG(),"onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG(),"onDestroy")
    }

    abstract fun TAG() : String

    abstract fun actionName() : String

    abstract fun actionHandler() : View.OnClickListener


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_title -> {
                Toast.makeText(this, "helloToolbar", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.app_bar_search -> {
                Toast.makeText(this, "helloToolbar", Toast.LENGTH_LONG).show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
