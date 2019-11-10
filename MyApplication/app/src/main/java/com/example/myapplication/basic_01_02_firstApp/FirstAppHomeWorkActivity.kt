package com.example.myapplication.basic_01_02_firstApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R

class FirstAppHomeWorkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)

        if(supportFragmentManager.findFragmentById(R.id.fragment_container) == null){
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, homeWorkFragment.newInstance()).commit()
        }
    }
}
