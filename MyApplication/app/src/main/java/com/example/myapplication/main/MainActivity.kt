package com.example.myapplication.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.work.OneTimeWorkRequestBuilder
import com.example.myapplication.BaseActivity
import com.example.myapplication.R
import com.example.myapplication.basic_01_02_firstApp.FirstAppActivity
import com.example.myapplication.basic_01_03_text_scrollingView.ScrollingTextActivity
import com.example.myapplication.basic_02_Activity.IntentSendActivity
import com.example.myapplication.sensorlist.SensorListActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MainActivity : BaseActivity(), AdapterView.OnItemSelectedListener {
    lateinit var mSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSpinner = findViewById<Spinner>(R.id.spinner).also { _spinner ->
            ArrayAdapter.createFromResource(
                this,
                R.array.actionActivity,
                android.R.layout.simple_spinner_item
            ).also { _adapter ->
                _adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                _spinner.adapter = _adapter
            }
            _spinner.onItemSelectedListener = this
        }

        findViewById<Button>(R.id.TestRxJava).setOnClickListener{_view->
            var chlist = listOf("a", "b", "c", "d", "e", "f")
            Observable.fromIterable(chlist).flatMap {
               Observable.just(it).delay(getTime(it)  ,TimeUnit.SECONDS)
            }
                /*.subscribe({
                    Log.d("testRxjava", it)
                }, {
                    Log.d("testRxjava", "throwable" + it)
                })*/
                .switchMap {

                    Observable.create<String>{ _emiter ->
                        var sleepTime = when(it){
                            "a" -> 100
                            "b" -> 90
                            "c" -> 80
                            "d" -> 70
                            "e" -> 60
                            else -> 20
                        }
                        Log.d("testRxjava", "sleepTime:" + sleepTime.toLong())
                        var timerTask = object : TimerTask(){
                            override fun run() {
                                if(_emiter.isDisposed){
                                    Log.d("testRxjava", "isDisposed.....")
                                    return;
                                }
                                Log.d("testRxjava", "onNext:{it}")
                                _emiter.onNext(it)
                            }
                        }
                        Timer().schedule(timerTask, sleepTime.toLong()*1000)

                    }.doOnTerminate(){
                        Log.d("testRxjava", "onTerminate")
                    }
                        .doOnError(){
                            Log.d("testRxjava", "onError")
                        }
                        .doOnDispose{
                            Log.d("testRxjava", "onDispose")
                        }
                        .subscribeOn(Schedulers.io())
                }
                .subscribe({
                    Log.d("testRxjava", it)
                }, {
                    Log.d("testRxjava", "throwable" + it)
                })

          /*  var ob1 = Observable.interval(0, 5, TimeUnit.SECONDS)
                .map{ "hello ob1 ${it.toString()} "}
                *//*.subscribe(){
                    Log.d("testRxjava", it)
                }*//*
            var ob2 = Observable.interval(0, 5,TimeUnit.SECONDS)
                .map{"hello ob2 ${it.toString()}"}
                *//*.subscribe(){
                    Log.d("testRxjava", it)
                }*//*
            Observables.combineLatest(ob1,ob2)
                .switchMap { _pair ->
                    Observable.create<String>{ _emiter ->
                        //Log.d("testRxjava", "currentThread: " + Thread.currentThread().name)
                        for(count in 1..1) {
                            try {
                                Thread.sleep(6 * 1000)
                            }
                            catch (e : Exception){
                                Log.d("testRxjava", "exception: " + e)
                            }
                            if(_emiter.isDisposed){
                                Log.d("testRxjava", "disposed")
                                break;
                            }
                        }
                        Log.d("testRxjava", "emiter..." + _pair.first + "->:<-" + _pair.second)
                        _emiter.onNext(_pair.first + "->:<-" + _pair.second)
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({_nextValue ->
                    Log.d("testRxjava", "subscribe value:" + _nextValue + " currentThread: " + Thread.currentThread().name)
                })
*/
        }
    }

    fun getTime(it : String) : Long{
        return when(it){
            "a" -> 10
            "b" -> 8
            "c" -> 6
            "d" -> 4
            "e" -> 2
            else -> 0
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //do nothing
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var activityItem = mSpinner.adapter.getItem(position)
        if(activityItem is String && activityItem != "None"){
            Class.forName("com.example.myapplication" + activityItem).also { _activity ->
                Intent(this, _activity).let { _intent ->
                    startActivity(_intent)
                    //finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        /*var classArray = arrayListOf<Class<*>>()
        classArray.add(IntentSendActivity::class.java)
        classArray.add(ScrollingTextActivity::class.java)
        classArray.add(FirstAppActivity::class.java)
        classArray.add(SensorListActivity::class.java)
        var selectIndex = 0
        Intent(this, classArray[0]).also {
            startActivity(it)
            finish()
        }*/
    }
}
