package com.example.myapplication.basic_03_android_test.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Entity(tableName = "todo_table")
data class Todo (
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
    var comment: String? = "",
    var priority: String? = TodoPriority.LOW.name
) : Serializable {
    constructor() : this(0, "", false,"", "", null)
    fun reset() {
        thing = ""
        completed = false
        description = ""
        imageUrl = ""
        targetTime = null
    }
}

enum class TodoPriority{
    EMERGENCY,
    HIGH,
    MIDDLE,
    LOW
}