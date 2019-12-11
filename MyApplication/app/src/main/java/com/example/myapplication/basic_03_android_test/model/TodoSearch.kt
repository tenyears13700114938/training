package com.example.myapplication.basic_03_android_test.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity
@Fts4(contentEntity = Todo::class)
data class TodoSearch(
    @ColumnInfo(name="content")
    var thing : String,
    var description : String? = ""
) {
}