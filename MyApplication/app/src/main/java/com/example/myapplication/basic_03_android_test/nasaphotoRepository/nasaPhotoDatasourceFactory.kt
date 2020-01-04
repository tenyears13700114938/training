package com.example.myapplication.basic_03_android_test.nasaphotoRepository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.myapplication.basic_03_android_test.model.nasaPhoto

class nasaPhotoDatasourceFactory : DataSource.Factory<String, nasaPhoto>() {
    val datasourceLiveData = MutableLiveData<nasaPhotoDatasource>()

    override fun create(): DataSource<String, nasaPhoto> {
        return nasaPhotoDatasource().also {
            datasourceLiveData.postValue(it)
        }
    }
}