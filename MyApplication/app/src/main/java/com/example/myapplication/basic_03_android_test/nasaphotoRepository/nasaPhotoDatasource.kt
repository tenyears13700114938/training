package com.example.myapplication.basic_03_android_test.nasaphotoRepository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.paging.ItemKeyedDataSource
import com.example.myapplication.basic_03_android_test.model.nasaPhoto
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class nasaPhotoDatasource : ItemKeyedDataSource<String, nasaPhoto>() {
    private val TAG = nasaPhotoDatasource::class.java.simpleName

    @RequiresApi(Build.VERSION_CODES.O)
    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<nasaPhoto>
    ) {
        (if(params.requestedInitialKey == null) "2019-12-28" else params.requestedInitialKey)?.also { _initialKey ->
            nasaRepository.getNasaPhotos(_initialKey, false, params.requestedLoadSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
        nasaRepository.getNasaPhotos(params.key, false, params.requestedLoadSize)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                Consumer<List<nasaPhoto>> {
                    callback.onResult(it)
                },
                Consumer<Throwable> {
                    Log.d(TAG, it.toString())
                }
            )
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<nasaPhoto>) {
        //do nothing...
    }

    override fun getKey(item: nasaPhoto): String {
        return item.date
    }
}