package com.psh.taskito

import android.app.Application
import com.psh.taskito.data.source.Repository

class TaskitoApplication : Application() {
    val taskRepository: Repository
        get() = ServiceLocator.provideTasksRepository(this)
}