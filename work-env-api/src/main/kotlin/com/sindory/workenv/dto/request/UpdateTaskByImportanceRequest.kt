package com.sindory.workenv.dto.request

import com.sindory.workenv.domain.entity.Importance

class UpdateTaskByImportanceRequest (
    val id: Long,
    val importance: Importance,
):BaseRequest(){
}