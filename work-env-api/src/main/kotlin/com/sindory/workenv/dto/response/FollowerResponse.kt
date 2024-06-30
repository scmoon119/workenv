package com.sindory.workenv.dto.response

import com.sindory.workenv.domain.entity.Importance

class FollowerResponse (
    val followerId:Long?=null,
    val nickName:String?=null,
    val description:String?=null,
){}