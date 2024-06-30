package com.sindory.workenv.dto.request

class DeleteLogRequest (
    val performanceLogIds:List<Long>? = null,
    val followerId:Long? = null
):BaseRequest(){
}