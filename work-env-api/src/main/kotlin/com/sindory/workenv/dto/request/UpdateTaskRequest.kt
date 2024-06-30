package com.sindory.workenv.dto.request

import com.sindory.workenv.domain.entity.Importance

class UpdateTaskRequest (
    val date: String,
    val importance: Importance,
    val priority: Int,
    val content:String,
    val tasks:List<CreateTaskRequest>
){
}