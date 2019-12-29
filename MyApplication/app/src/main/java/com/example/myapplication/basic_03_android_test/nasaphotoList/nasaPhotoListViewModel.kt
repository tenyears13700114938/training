package com.example.myapplication.basic_03_android_test.nasaphotoList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.myapplication.basic_03_android_test.model.nasaPhoto
import com.example.myapplication.basic_03_android_test.nasaphotoRepository.nasaPhotoDatasourceFactory
import com.example.myapplication.basic_03_android_test.nasaphotoRepository.nasaRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.properties.Delegates

class nasaPhotoListViewModel : ViewModel() {
    val nasaPhotoList : LiveData<PagedList<nasaPhoto>>  by lazy {
            var pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(10)
                .setPageSize(20).build()

            var photoFactory = nasaPhotoDatasourceFactory()
            LivePagedListBuilder<String, nasaPhoto>(photoFactory, pagedListConfig)
                .build()
    }
}
