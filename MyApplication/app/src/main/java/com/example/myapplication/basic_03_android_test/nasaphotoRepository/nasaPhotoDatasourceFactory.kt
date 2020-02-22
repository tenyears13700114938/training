package com.example.myapplication.basic_03_android_test.nasaphotoRepository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.myapplication.basic_03_android_test.model.NasaPhoto

class nasaPhotoDatasourceFactory : DataSource.Factory<String, NasaPhoto>() {
    val datasourceLiveData = MutableLiveData<nasaPhotoDatasource>()

    override fun create(): DataSource<String, NasaPhoto> {
        return nasaPhotoDatasource().also {
            datasourceLiveData.postValue(it)
        }
    }
}