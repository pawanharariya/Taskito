package com.psh.taskito.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task @JvmOverloads constructor(
    @ColumnInfo(name = "title")
    val title: String = "",

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "completed")
    val isCompleted: Boolean = false,

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    var id: String
) {

    val titleForList: String
        get() = title.ifEmpty { description }
    val isActive
        get() = !isCompleted

    val isEmpty
        get() = title.isEmpty() || description.isEmpty()
}

