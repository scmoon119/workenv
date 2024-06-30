package com.sindory.workenv.dto.response

import com.sindory.workenv.domain.entity.Follower

class PerformanceLogResponse(
    val performanceLogId: Long?=null,
    val date: String?=null,
    val content:String?=null,
    val followerId: Long?=null,
    val followerName: String?=null,
    ) {
}