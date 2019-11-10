package com.example.myapplication.sensorlist

import android.os.Bundle
import com.example.myapplication.BaseActivity
import com.example.myapplication.R

class SensorListActivity : BaseActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)
        if(null == supportFragmentManager.findFragmentById(R.id.fragment_container)){
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SensorListFragment.newInstance()).commit()
        }
    }
}