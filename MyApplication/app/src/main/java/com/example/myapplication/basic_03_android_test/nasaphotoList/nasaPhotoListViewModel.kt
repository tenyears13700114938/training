package com.example.myapplication.basic_03_android_test.nasaphotoList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.example.myapplication.basic_03_android_test.model.nasaPhoto
import kotlin.properties.Delegates

class nasaPhotoListViewModel : ViewModel() {
    val nasaPhotoList : LiveData<PagedList<nasaPhoto>>  by lazy {
        MutableLiveData<PagedList<nasaPhoto>>()
    }
}
