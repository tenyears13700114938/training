package com.example.myapplication.basic_03_android_test.nasaPhotoContentDetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.model.nasaPhotoEntity

class nasaPhotoDetailsActivity : AppCompatActivity() {
    lateinit var detailsViewModel : nasaPhotoDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nava_photo_details_activity)
        detailsViewModel = ViewModelProviders.of(this).get(nasaPhotoDetailsViewModel::class.java)
        intent.getSerializableExtra(EXTRA_NASA_PHOTO_PARAM)?.also {
            if(it is nasaPhotoEntity){
                detailsViewModel.nasaPhotoInfo = it
            }
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, nasaPhotoDetailsFragment.newInstance())
                .commitNow()
        }
    }

    companion object {
        val EXTRA_NASA_PHOTO_PARAM = "EXTRA_NASA_PHOTO_PARAM"
    }

}
