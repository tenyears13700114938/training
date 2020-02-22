package com.example.myapplication.basic_03_android_test.nasaPhotoContentDetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.model.NasaPhotoEntity

class NasaPhotoDetailsActivity : AppCompatActivity() {
    lateinit var detailsViewModel : NasaPhotoDetailsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nava_photo_details_activity)
        detailsViewModel = ViewModelProviders.of(this).get(NasaPhotoDetailsViewModel::class.java)
        intent.getSerializableExtra(EXTRA_NASA_PHOTO_PARAM)?.also {
            if(it is NasaPhotoEntity){
                detailsViewModel.nasaPhotoInfo = it
            }
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, NasaPhotoDetailsFragment.newInstance())
                .commitNow()
        }
    }

    companion object {
        val EXTRA_NASA_PHOTO_PARAM = "EXTRA_NASA_PHOTO_PARAM"
    }
}
