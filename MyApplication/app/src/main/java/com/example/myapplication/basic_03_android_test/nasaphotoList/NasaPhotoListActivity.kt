package com.example.myapplication.basic_03_android_test.nasaphotoList

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.android.synthetic.main.activity_navi_common.*

class NasaPhotoListActivity : AppCompatActivity() {
    private lateinit var photoListViewModel: NasaPhotoListViewModel
    private lateinit var mNavHostFragment: NavHostFragment
    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nasa_photo_list)
        if (savedInstanceState == null) {
            /*supportFragmentManager.beginTransaction()
                .replace(R.id.container, nasaPhotoListFragment.newInstance())
                .commitNow()*/
            /*toolbar.title = "NasaPhoto List"*/
            mNavHostFragment = supportFragmentManager.findFragmentById(R.id.navi_host_fragment) as NavHostFragment? ?: return
            mNavController = mNavHostFragment.navController
            mNavController.graph = mNavController.navInflater.inflate(R.navigation.navi_photo_navigation)

            photoListViewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory{
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return NasaPhotoListViewModel(applicationContext) as T
                }

            }).get(NasaPhotoListViewModel::class.java)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

}
