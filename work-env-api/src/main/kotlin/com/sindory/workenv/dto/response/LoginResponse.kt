package com.sindory.workenv.dto.response

import java.util.*

data class LoginResponse(
    var userId: String,
    var name: String?,
    var token: UUID? = null,
    var expiredDt: Long? = null,
): SaveResultResponse(){
    constructor(userId: String, message:String) : this(userId, null, null, null){
        this.message = message
    }
}