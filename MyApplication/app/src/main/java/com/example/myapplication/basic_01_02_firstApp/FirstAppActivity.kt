package com.example.myapplication.basic_01_02_firstApp

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.myapplication.BaseActivity
import com.example.myapplication.R
import javax.inject.Inject

class FirstAppActivity : BaseActivity() {
    lateinit var mCountTextView : TextView
    @Inject
    lateinit var mCountViewModel : CountViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_app)

        mCountTextView = findViewById(R.id.show_count)
        mCountViewModel.getCountViewModel().observe(this, Observer(){
            mCountTextView.text = it.toString()
        })

        findViewById<Button>(R.id.button_count).also {
            it.setOnLongClickListener(){ _view ->
                var intent = Intent(_view.context, FirstAppHomeWorkActivity::class.java)
                startActivity(intent)
                true
            }
            /*it.setOnClickListener(){_view ->

            }*/
        }
    }

    fun showToast(view: View) {
        /*Toast.makeText(this, R.string.toast_message, Toast.LENGTH_SHORT).let {
            it.setGravity(Gravity.CENTER, 0, 0  )
            it.show()
        }*/
        Intent(this, ShowHelloCountActivity::class.java).let { _intent ->
            _intent.putExtra(ShowHelloCountActivity.COUNT_EXTRA_PARAMETER, mCountViewModel.countStep.value)
            startActivity(_intent)
        }
    }
    fun countUp(view: View) {
        if(view is Button){
            mCountTextView.text.toString().toInt().let {
                mCountViewModel.getCountViewModel().value = it + 1
            }
        }
    }
}
