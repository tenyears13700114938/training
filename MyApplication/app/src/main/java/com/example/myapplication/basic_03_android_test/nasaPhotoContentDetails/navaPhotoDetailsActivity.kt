package com.example.myapplication.basic_03_android_test.nasaPhotoContentDetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R

class navaPhotoDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nava_photo_details_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, nasaPhotoDetailsFragment.newInstance())
                .commitNow()
        }
    }

}
