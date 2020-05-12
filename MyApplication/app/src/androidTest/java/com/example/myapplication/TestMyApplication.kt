package com.example.myapplication

class TestMyApplication : MyApplication() {
    override fun createAppComponent() {
        appComponent = DaggerTestAppComponent.builder().myApp(this).build()
        appComponent.inject(this)
    }

}