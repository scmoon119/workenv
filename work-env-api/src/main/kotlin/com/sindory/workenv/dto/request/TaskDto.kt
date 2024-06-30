package com.sindory.workenv.dto.request

import com.sindory.workenv.domain.entity.Importance
import com.sindory.workenv.domain.entity.TaskStatus

class TaskDto(
    val taskId:Long,
    val date: String,
    val importance: Importance,
    val priority: Int,
    val content:String,
    val status: TaskStatus,
    ) {
}