package com.example.myapplication.basic_03_android_test.activityCommon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.view.iterator
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.nasaphotoList.NasaPhotoListActivity
import com.example.myapplication.basic_03_android_test.todoList.TodoListActivity
import com.example.myapplication.util.searchViewUtil
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_navi_common.*

open class NavCommonActivity : AppCompatActivity() {
    protected lateinit var mHostFragment: NavHostFragment
    protected lateinit var mNaviView : NavigationView
    protected lateinit var mNavController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navi_common)

        mHostFragment = supportFragmentManager.findFragmentById(R.id.navi_host_fragment) as NavHostFragment? ?: return
        mNavController = mHostFragment.navController
        //configure toolbar
        toolbar.setNavigationIcon(R.drawable.navi_menu)
        toolbar.setTitleTextColor(resources.getColor(R.color.colorWhite, null))
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            findViewById<DrawerLayout>(R.id.root_drawer).openDrawer(GravityCompat.START)
        }

        //configure navigationView
        mNaviView = findViewById(R.id.navi_slide_menu)
        configureNavView(mNaviView)
    }

    protected fun configureNavView(navigationView: NavigationView?) {
        navigationView?.let {
            val drawerLayoutParams : DrawerLayout.LayoutParams = it.layoutParams as DrawerLayout.LayoutParams
            drawerLayoutParams.width = resources.displayMetrics.widthPixels * 5 / 6
            it.layoutParams = drawerLayoutParams

            searchViewUtil(
                it.getHeaderView(0) as ViewGroup,
                R.id.navi_slide_menu_image
            )?.also { _imageView ->
                if (_imageView is ImageView) {
                    val imageViewLayoutParams: LinearLayout.LayoutParams =
                        _imageView.layoutParams as LinearLayout.LayoutParams
                    imageViewLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                    _imageView.layoutParams = imageViewLayoutParams
                    Glide.with(this).load(R.drawable.children_1822688_960_720)
                        .into(_imageView)
                }
            }
            val menuIterator = it.menu.iterator()
            while (menuIterator.hasNext()) {
                menuIterator.next().let {
                    it.setOnMenuItemClickListener() { item ->
                        findViewById<DrawerLayout>(R.id.root_drawer).closeDrawer(
                            GravityCompat.START
                        )
                        when (item.itemId) {
                            R.id.todo -> {
                                Intent(applicationContext, TodoListActivity::class.java).also {
                                    startActivity(it)
                                }
                                true
                            }
                            R.id.nasa_world -> {
                                Intent(applicationContext, NasaPhotoListActivity::class.java).also {
                                    startActivity(it)
                                }
                                true
                            }
                            R.id.next_what ->  {
                                Toast.makeText(applicationContext, "to be future....", Toast.LENGTH_SHORT).show()
                                true
                            }
                            else -> true
                        }
                    }
                }
            }
        }
    }

    fun setTitle(title : String){
        toolbar.title = title
    }

    fun setMenuVisibility(visibility : Boolean){
        toolbar.menu.iterator().let { _iterator ->
            while(_iterator.hasNext()){
                _iterator.next().setVisible(visibility)
            }
        }
    }
}
