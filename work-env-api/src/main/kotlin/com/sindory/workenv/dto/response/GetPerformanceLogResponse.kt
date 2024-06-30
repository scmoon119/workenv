package com.sindory.workenv.dto.response

class GetPerformanceLogResponse(
    var message:String ? = null,  // 성공은 항상 OK
    var performanceLogs:List<PerformanceLogResponse>? = null,

    )
{
}