package com.example.myapplication.basic_03_android_test.nasaphotoList

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class nasaPhotoListFragment : Fragment() {

    companion object {
        fun newInstance() =
            nasaPhotoListFragment()
    }

    private lateinit var viewModel: nasaPhotoListViewModel
    private lateinit var mNasaPhotoListView : RecyclerView
    private val mNasaPhotoListAdapter : nasaPhotoListAdapter = nasaPhotoListAdapter()
    private lateinit var mGridLayoutManager : GridLayoutManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.nasa_photo_list_fragment, container, false).also {_rootView ->
            mNasaPhotoListView = _rootView.findViewById<RecyclerView>(R.id.nasa_photo_list).also {
                it.adapter = mNasaPhotoListAdapter
                mGridLayoutManager = GridLayoutManager(context!!, 1)
                it.layoutManager = mGridLayoutManager
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(nasaPhotoListViewModel::class.java)
        viewModel.nasaPhotoList.observe(this){_pagedPhotoList ->

        }
    }

}
