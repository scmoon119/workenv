package com.sindory.workenv.dto.request

import com.sindory.workenv.domain.entity.Importance

class UpdateTaskByContentRequest (
    val id: Long,
    val content:String,
): BaseRequest(){
}