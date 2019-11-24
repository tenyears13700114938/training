package com.example.myapplication.basic_03_android_test.todoDetail

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import kotlinx.android.synthetic.main.activity_navi_common.*

val DETAIL_ACTIVITY_START_PARAM_TO_DO_INFO = "START_PARAM_TO_DO"
class TodoDetailActivity : NavCommonActivity(), TodoDetailFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        //todo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.title = "Todo Detail"
        val bundle = Bundle()
        bundle.putSerializable(
            "Todo_Detail", intent?.getSerializableExtra(
                DETAIL_ACTIVITY_START_PARAM_TO_DO_INFO
            )
        )
        val graph = mNavController.navInflater.inflate(R.navigation.todo_detail_navigation)
        mNavController.setGraph(graph, bundle)
    }
}
