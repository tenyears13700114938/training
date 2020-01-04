package com.example.myapplication.basic_03_android_test.nasaphotoList

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.myapplication.basic_03_android_test.model.nasaPhoto
import com.example.myapplication.basic_03_android_test.model.nasaPhotoEntity
import com.example.myapplication.basic_03_android_test.nasaphotoRepository.nasaPhotoDatasourceFactory
import com.example.myapplication.basic_03_android_test.nasaphotoRepository.nasaRepository
import com.example.myapplication.util.getDateStr
import com.example.myapplication.util.mapNasaphotoToEntity
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors

class nasaPhotoListViewModel(var appContext : Context) : ViewModel() {
    private val TAG = nasaPhotoListViewModel::class.java.simpleName
    private var photoFactory = nasaPhotoDatasourceFactory()
    val loadStatusLiveData = Transformations.switchMap(photoFactory.datasourceLiveData){
        it.loadStatusLiveDate
    }
    private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

    val nasaPhotoList : LiveData<PagedList<nasaPhotoEntity>>  by lazy {
            var pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(40)
                .setPageSize(20).build()
            LivePagedListBuilder<Int, nasaPhotoEntity>(nasaRepository.getNasaPhotosFromDb(appContext), pagedListConfig)
                .setBoundaryCallback(object : PagedList.BoundaryCallback<nasaPhotoEntity>(){
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onZeroItemsLoaded() {
                        val startDate =
                            LocalDate.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd"))
                        Log.d(TAG, "onZeroItemsLoaded..." + startDate)
                        loadNasaphotoEntitys(startDate, 40)
                    }

                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onItemAtEndLoaded(itemAtEnd: nasaPhotoEntity) {
                        val startDate = getDateStr(itemAtEnd.date, -1)
                        Log.d(TAG, "onItemAtEndLoaded..." + startDate)
                        loadNasaphotoEntitys(startDate, 20)
                    }

                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onItemAtFrontLoaded(itemAtFront: nasaPhotoEntity) {
                        var startDateStr = getDateStr(itemAtFront.date, 20)
                        val startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ofPattern("uuuu-MM-dd"))
                        val daysBetween = ChronoUnit.DAYS.between(startDate, LocalDate.now())
                        if(daysBetween >= 0){
                            loadNasaphotoEntitys(startDate.format(DateTimeFormatter.ofPattern("uuuu-MM-dd")), 20)
                            Log.d(TAG, "onItemAtFrontLoaded..." + startDate + " page 20")
                        }
                        else {
                            startDateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd"))
                            loadNasaphotoEntitys(startDateStr, 20 + daysBetween.toInt())
                            Log.d(TAG, "onItemAtFrontLoaded..." + startDateStr + " page:" + (20 + daysBetween.toInt()))
                        }
                    }

                    @RequiresApi(Build.VERSION_CODES.O)
                    private fun loadNasaphotoEntitys(startDate: String, size: Int) {
                        IO_EXECUTOR.submit() {
                            nasaRepository.getNasaPhotos(startDate, false, size)
                                .subscribeOn(Schedulers.io())
                                .subscribe(
                                    Consumer<List<nasaPhoto>> {
                                        nasaRepository.insertNasaPhotos(appContext, it.map {
                                            mapNasaphotoToEntity(it)
                                        })
                                    }
                                )
                        }
                    }
                })
                .build()
    }
}
