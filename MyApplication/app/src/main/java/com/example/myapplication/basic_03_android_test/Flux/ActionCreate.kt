package com.example.myapplication.basic_03_android_test.Flux

data class ActionCreate(val run : suspend () -> Action)