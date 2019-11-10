package com.example.myapplication.basic_02_Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.myapplication.R

class ImplicitIntentReceiverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_implicit_intent_receiver)

        intent?.also {
            findViewById<TextView>(R.id.receiveUrlTextView)?.text = if (it.data != null)  it.data.toString() else ""
        }
    }
}
