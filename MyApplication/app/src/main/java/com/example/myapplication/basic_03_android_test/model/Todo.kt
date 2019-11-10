package com.example.myapplication.basic_03_android_test.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Entity(tableName = "todo_table")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id : Long,
    @ColumnInfo(name="content")
    val thing : String,
    var completed : Boolean,
    val description : String? = "") : Serializable