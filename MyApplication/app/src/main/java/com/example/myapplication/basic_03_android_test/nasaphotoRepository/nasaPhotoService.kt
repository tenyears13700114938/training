package com.example.myapplication.basic_03_android_test.nasaphotoRepository

import com.example.myapplication.basic_03_android_test.model.NasaPhoto
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

val appKey = "EPmMkka9hVYIi2j0Ceuc50JccP8AypWf3W4H6zRQ"
interface nasaPhotoService {
    @GET("apod")
    fun getNasaPhoto(@Query("date") dateVal: String,
                     @Query("hd") hdVal: Boolean = false,
                     @Query("api_key") apiKey: String = appKey): Observable<NasaPhoto>

    @GET
    fun downloadPhoto(@Url url : String) : Call<ResponseBody>
}