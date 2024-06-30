package com.sindory.workenv.dto.request

import com.sindory.workenv.domain.entity.Importance
import com.sindory.workenv.domain.entity.TaskStatus

class UpdateTaskByStatusRequest (
    val id: Long,
    val status: TaskStatus,
    val dayToPostpone: Int,
    val deligatedUser:String,
):BaseRequest(){
}