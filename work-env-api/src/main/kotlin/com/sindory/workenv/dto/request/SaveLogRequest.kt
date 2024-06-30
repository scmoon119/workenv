package com.sindory.workenv.dto.request

class SaveLogRequest (
    val date:String? = null,
    val log:String? = null,
    val followerId:Long? = null,
    val logId:Long? = null,
):BaseRequest() {}