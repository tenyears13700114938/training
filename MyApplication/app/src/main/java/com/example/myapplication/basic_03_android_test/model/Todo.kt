package com.example.myapplication.basic_03_android_test.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Entity(tableName = "todo_table")
data class Todo constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    @ColumnInfo(name = "content")
    var thing: String,
    var completed: Boolean,
    var description: String? = "",
    @ColumnInfo(name = "image_url")
    var imageUrl: String? = "",
    @ColumnInfo(name = "target_time")
    var targetTime: Long? = 0,
    var comment: String? = ""
) : Serializable {
    constructor() : this(0, "", false,"", "", null)
    fun reset() {
        thing = ""
        completed = false
        description = ""
        imageUrl = ""
        targetTime = null
    }
    fun empty() : Boolean {
        return thing.equals("") && (completed == false) && description.equals("") && imageUrl.equals("") && (targetTime == null)
    }
}