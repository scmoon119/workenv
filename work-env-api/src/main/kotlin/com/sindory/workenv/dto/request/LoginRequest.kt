package com.sindory.workenv.dto.request

import jakarta.persistence.Id

data class LoginRequest (
    val userId: String,
    val password:String?,
){
}