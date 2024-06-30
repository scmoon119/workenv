package com.sindory.workenv.dto.request


class CreateFollowerRequest (
    val followerId:Long? = null,
    val nickName:String,
    val description:String,
): BaseRequest(){

}
