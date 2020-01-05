package com.example.myapplication.basic_03_android_test.nasaphotoList

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import com.example.myapplication.basic_03_android_test.model.nasaPhotoLoadStatus
import com.example.myapplication.basic_03_android_test.nasaPhotoContentDetails.nasaPhotoDetailsActivity
import com.example.myapplication.basic_03_android_test.nasaPhotoContentDetails.nasaPhotoDetailsActivity.Companion.EXTRA_NASA_PHOTO_PARAM
import com.example.myapplication.basic_03_android_test.nasaPhotoContentDetails.nasaPhotoDetailsFragment
import kotlinx.android.synthetic.main.activity_navi_common.*

class nasaPhotoListFragment : Fragment() {

    companion object {
        fun newInstance() =
            nasaPhotoListFragment()
    }

    private lateinit var viewModel: nasaPhotoListViewModel
    private lateinit var mNasaPhotoListView: RecyclerView
    private lateinit var mProgressBar: ProgressBar
    private val mNasaPhotoListAdapter: nasaPhotoListAdapter = nasaPhotoListAdapter()
    private lateinit var mGridLayoutManager: GridLayoutManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.nasa_photo_list_fragment, container, false)
            .also { _rootView ->
                mNasaPhotoListView =
                    _rootView.findViewById<RecyclerView>(R.id.nasa_photo_list).also {
                        it.adapter = mNasaPhotoListAdapter
                        mGridLayoutManager = GridLayoutManager(context!!, 2)
                        it.layoutManager = mGridLayoutManager
                    }
                mProgressBar = _rootView.findViewById(R.id.progressBar)
                mNasaPhotoListAdapter.clickPublishSubject
                    .subscribe {
                       /* var detailAction = nasaPhotoListFragmentDirections.actionNasaPhotoListFragmentToNavaPhotoDetailsFragment(it)
                        _rootView.findNavController().navigate(detailAction)*/
                        val intent = Intent(activity, nasaPhotoDetailsActivity::class.java)
                        intent.putExtra(EXTRA_NASA_PHOTO_PARAM, it)
                        startActivity(intent)
                    }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return nasaPhotoListViewModel(this@nasaPhotoListFragment.requireContext()) as T
            }

        }).get(nasaPhotoListViewModel::class.java)
        viewModel.nasaPhotoList.observe(this) { _pagedPhotoList ->
            mNasaPhotoListAdapter.submitList(_pagedPhotoList)
        }
        viewModel.loadStatusLiveData.observe(this) {
            when (it) {
                nasaPhotoLoadStatus.LOADING -> {
                    mProgressBar.visibility = View.VISIBLE
                }
                else -> {
                    mProgressBar.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as NavCommonActivity).run {
            setTitle("nasaPhoto List")
        }
    }
}
