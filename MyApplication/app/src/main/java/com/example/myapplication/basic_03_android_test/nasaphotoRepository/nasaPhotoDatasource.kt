package com.example.myapplication.basic_03_android_test.nasaphotoRepository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import com.example.myapplication.basic_03_android_test.model.nasaPhoto
import com.example.myapplication.basic_03_android_test.model.nasaPhotoLoadStatus
import com.example.myapplication.util.getDateStr
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class nasaPhotoDatasource : ItemKeyedDataSource<String, nasaPhoto>() {
    private val TAG = nasaPhotoDatasource::class.java.simpleName
    val loadStatusLiveDate = MutableLiveData<nasaPhotoLoadStatus>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<nasaPhoto>
    ) {
        loadStatusLiveDate.postValue(nasaPhotoLoadStatus.LOADING)
        val startDate = LocalDate.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd"))
        (if(params.requestedInitialKey == null) startDate else params.requestedInitialKey)?.also { _initialKey ->
            nasaRepository.getNasaPhotos(_initialKey, false, params.requestedLoadSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate {
                    loadStatusLiveDate.postValue(nasaPhotoLoadStatus.FINISHED)
                }
                .subscribe(
                    Consumer<List<nasaPhoto>> {
                        callback.onResult(it)
                    },
                    Consumer<Throwable> {
                        Log.d(TAG, it.toString())
                    }
                )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<nasaPhoto>) {
        var nextKey = getDateStr(params.key, -1)
        if (!Objects.equals(nextKey, "")) {
            loadStatusLiveDate.postValue(nasaPhotoLoadStatus.LOADING)
            nasaRepository.getNasaPhotos(nextKey, false, params.requestedLoadSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate{
                    loadStatusLiveDate.postValue(nasaPhotoLoadStatus.FINISHED)
                }
                .subscribe(
                    Consumer<List<nasaPhoto>> {
                        callback.onResult(it)
                    },
                    Consumer<Throwable> {
                        Log.d(TAG, it.toString())
                    }
                )
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<nasaPhoto>) {
        //do nothing...
    }

    override fun getKey(item: nasaPhoto): String {
        return item.date
    }
}