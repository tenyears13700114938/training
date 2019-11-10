package com.example.myapplication.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.myapplication.BaseActivity
import com.example.myapplication.R
import com.example.myapplication.basic_01_02_firstApp.FirstAppActivity
import com.example.myapplication.basic_01_03_text_scrollingView.ScrollingTextActivity
import com.example.myapplication.basic_02_Activity.IntentSendActivity
import com.example.myapplication.sensorlist.SensorListActivity

class MainActivity : BaseActivity(), AdapterView.OnItemSelectedListener {
    lateinit var mSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSpinner = findViewById<Spinner>(R.id.spinner).also { _spinner ->
            ArrayAdapter.createFromResource(
                this,
                R.array.actionActivity,
                android.R.layout.simple_spinner_item
            ).also { _adapter ->
                _adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                _spinner.adapter = _adapter
            }
            _spinner.onItemSelectedListener = this
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //do nothing
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var activityItem = mSpinner.adapter.getItem(position)
        if(activityItem is String && activityItem != "None"){
            Class.forName("com.example.myapplication" + activityItem).also { _activity ->
                Intent(this, _activity).let { _intent ->
                    startActivity(_intent)
                    //finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        /*var classArray = arrayListOf<Class<*>>()
        classArray.add(IntentSendActivity::class.java)
        classArray.add(ScrollingTextActivity::class.java)
        classArray.add(FirstAppActivity::class.java)
        classArray.add(SensorListActivity::class.java)
        var selectIndex = 0
        Intent(this, classArray[0]).also {
            startActivity(it)
            finish()
        }*/
    }
}
