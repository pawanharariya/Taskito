package com.psh.taskito.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tasks")
data class Task constructor(
    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "completed")
    var isCompleted: Boolean = false,

    @PrimaryKey @ColumnInfo(name = "id")
    var id: String = UUID.randomUUID().toString()
) {

    val titleForList: String
        get() = title.ifEmpty { description }
    val isActive
        get() = !isCompleted

    val isEmpty
        get() = title.isEmpty() || description.isEmpty()
}

