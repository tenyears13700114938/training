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
import com.example.myapplication.basic_03_android_test.model.NasaPhoto
import com.example.myapplication.basic_03_android_test.model.NasaPhotoEntity
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

class NasaPhotoListViewModel(var appContext : Context) : ViewModel() {
    private val TAG = NasaPhotoListViewModel::class.java.simpleName
    private var photoFactory = nasaPhotoDatasourceFactory()
    val loadStatusLiveData = Transformations.switchMap(photoFactory.datasourceLiveData){
        it.loadStatusLiveDate
    }
    private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

    val nasaPhotoList : LiveData<PagedList<NasaPhotoEntity>>  by lazy {
            val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(40)
                .setPageSize(20).build()
            LivePagedListBuilder<Int, NasaPhotoEntity>(nasaRepository.getNasaPhotosFromDb(appContext), pagedListConfig)
                .setBoundaryCallback(object : PagedList.BoundaryCallback<NasaPhotoEntity>(){
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onZeroItemsLoaded() {
                        val startDate =
                            LocalDate.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd"))
                        Log.d(TAG, "onZeroItemsLoaded...${startDate}")
                        loadNasaPhotoEntitys(startDate, 40)
                    }

                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onItemAtEndLoaded(itemAtEnd: NasaPhotoEntity) {
                        val startDate = getDateStr(itemAtEnd.date, -1)
                        Log.d(TAG, "onItemAtEndLoaded...${startDate}")
                        loadNasaPhotoEntitys(startDate, 20)
                    }

                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onItemAtFrontLoaded(itemAtFront: NasaPhotoEntity) {
                        var startDateStr = getDateStr(itemAtFront.date, 20)
                        val startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ofPattern("uuuu-MM-dd"))
                        val daysBetween = ChronoUnit.DAYS.between(startDate, LocalDate.now())
                        if(daysBetween >= 0){
                            loadNasaPhotoEntitys(startDate.format(DateTimeFormatter.ofPattern("uuuu-MM-dd")), 20)
                            Log.d(TAG, "onItemAtFrontLoaded...${startDate} page 20")
                        }
                        else {
                            startDateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd"))
                            loadNasaPhotoEntitys(startDateStr, 20 + daysBetween.toInt())
                            Log.d(TAG, "onItemAtFrontLoaded...${startDateStr} page: ${20 + daysBetween.toInt()}")
                        }
                    }

                    @RequiresApi(Build.VERSION_CODES.O)
                    private fun loadNasaPhotoEntitys(startDate: String, size: Int) {
                        IO_EXECUTOR.submit{
                            nasaRepository.getNasaPhotos(startDate, false, size)
                                .subscribeOn(Schedulers.io())
                                .subscribe(
                                    Consumer<List<NasaPhoto>> {
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
