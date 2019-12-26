package com.example.myapplication.basic_03_android_test.nasaphotoList

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import kotlinx.android.synthetic.main.activity_navi_common.*

class nasaPhotoListActivity : NavCommonActivity() {
    private lateinit var nasaPhotoListViewModel: nasaPhotoListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            /*supportFragmentManager.beginTransaction()
                .replace(R.id.container, nasaPhotoListFragment.newInstance())
                .commitNow()*/
            toolbar.title = "navaPhoto List"
            mNavController.graph = mNavController.navInflater.inflate(R.navigation.navi_photo_navigation)

            nasaPhotoListViewModel = ViewModelProviders.of(this).get(nasaPhotoListViewModel::class.java)
        }
    }

}
