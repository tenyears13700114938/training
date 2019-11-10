package com.example.myapplication.basic_01_02_firstApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.myapplication.R

class ShowHelloCountActivity : AppCompatActivity() {

    companion object {
        val COUNT_EXTRA_PARAMETER = "COUNT_EXTRA_PARAMETER"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_hello_count)

        findViewById<TextView>(R.id.helloCountTextView).also { _textView ->
            var sb: StringBuilder = StringBuilder()
            _textView.text = sb.append("HELLO!\n")
                .append(intent?.getIntExtra(COUNT_EXTRA_PARAMETER, 0))
                .toString()
        }
    }
}
