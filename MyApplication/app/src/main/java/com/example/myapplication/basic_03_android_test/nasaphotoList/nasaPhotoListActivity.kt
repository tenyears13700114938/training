package com.example.myapplication.basic_03_android_test.nasaphotoList

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import kotlinx.android.synthetic.main.activity_navi_common.*

class nasaPhotoListActivity : NavCommonActivity() {
    private lateinit var photoListViewModel: nasaPhotoListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            /*supportFragmentManager.beginTransaction()
                .replace(R.id.container, nasaPhotoListFragment.newInstance())
                .commitNow()*/
            toolbar.title = "navaPhoto List"
            mNavController.graph = mNavController.navInflater.inflate(R.navigation.navi_photo_navigation)

            photoListViewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory{
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return nasaPhotoListViewModel(applicationContext) as T
                }

            }).get(nasaPhotoListViewModel::class.java)
        }
    }

}
