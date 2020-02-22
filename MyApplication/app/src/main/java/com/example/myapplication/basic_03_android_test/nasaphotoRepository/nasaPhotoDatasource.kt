package com.example.myapplication.basic_03_android_test.nasaphotoRepository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import com.example.myapplication.basic_03_android_test.model.NasaPhoto
import com.example.myapplication.basic_03_android_test.model.NasaPhotoLoadStatus
import com.example.myapplication.util.getDateStr
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class nasaPhotoDatasource : ItemKeyedDataSource<String, NasaPhoto>() {
    private val TAG = nasaPhotoDatasource::class.java.simpleName
    val loadStatusLiveDate = MutableLiveData<NasaPhotoLoadStatus>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<NasaPhoto>
    ) {
        loadStatusLiveDate.postValue(NasaPhotoLoadStatus.LOADING)
        val startDate = LocalDate.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd"))
        (if(params.requestedInitialKey == null) startDate else params.requestedInitialKey)?.also { _initialKey ->
            nasaRepository.getNasaPhotos(_initialKey, false, params.requestedLoadSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate {
                    loadStatusLiveDate.postValue(NasaPhotoLoadStatus.FINISHED)
                }
                .subscribe(
                    Consumer<List<NasaPhoto>> {
                        callback.onResult(it)
                    },
                    Consumer<Throwable> {
                        Log.d(TAG, it.toString())
                    }
                )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<NasaPhoto>) {
        var nextKey = getDateStr(params.key, -1)
        if (!Objects.equals(nextKey, "")) {
            loadStatusLiveDate.postValue(NasaPhotoLoadStatus.LOADING)
            nasaRepository.getNasaPhotos(nextKey, false, params.requestedLoadSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate{
                    loadStatusLiveDate.postValue(NasaPhotoLoadStatus.FINISHED)
                }
                .subscribe(
                    Consumer<List<NasaPhoto>> {
                        callback.onResult(it)
                    },
                    Consumer<Throwable> {
                        Log.d(TAG, it.toString())
                    }
                )
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<NasaPhoto>) {
        //do nothing...
    }

    override fun getKey(item: NasaPhoto): String {
        return item.date
    }
}