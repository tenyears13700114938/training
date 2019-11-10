package com.example.myapplication.basic_02_Activity

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

import kotlinx.android.synthetic.main.activity_implicit_intent.*

class ImplicitIntentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_implicit_intent)
        setSupportActionBar(toolbar)

    }

}
