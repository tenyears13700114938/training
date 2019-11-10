package com.example.myapplication.sensordetails

import android.os.Bundle
import com.example.myapplication.BaseActivity
import com.example.myapplication.R

class SensorDetailsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)
        if(supportFragmentManager.findFragmentById(R.id.fragment_container) == null){
            SensorDetailsFragment.newInstance(intent.getIntExtra(SENSOR_TYPE, 0)).also {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, it).commit()
            }
        }
    }

    companion object {
        var SENSOR_TYPE = "SENSOR_TYPE"
    }
}
