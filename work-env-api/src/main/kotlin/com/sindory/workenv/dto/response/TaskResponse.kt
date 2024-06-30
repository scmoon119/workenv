package com.sindory.workenv.dto.response

import com.sindory.workenv.domain.entity.Importance
import com.sindory.workenv.domain.entity.Task
import com.sindory.workenv.domain.entity.TaskStatus
import jakarta.persistence.*
import org.jetbrains.annotations.NotNull

data class TaskResponse(
    val importance: Importance,
    val priority: Int,
    val content:String,
    val status:String,
    val taskId : Long?=null,
    ) {
//    init {
//        if(name.isBlank()){
//            throw IllegalArgumentException("이름은 비어 있을 수 없습니다")
//        }
//    }
}