package com.sindory.workenv.dto.response

class GetFollowersResponse(
    var message:String ? = null,  // 성공은 항상 OK
    var followerList:List<FollowerResponse>? = null,
    )
{
}