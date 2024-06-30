package com.sindory.workenv.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.*

@Entity
class Auth (
    var userId: String,
    var password:String,
    var token: UUID?=null,
    var expireDt:Long?=null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val memberId: Long? = null,
){

    init {
        if(userId.isBlank() || password.isBlank()){
            throw IllegalArgumentException("아이디와 암호는 비어 있을 수 없습니다")
        }
    }
//    fun createToken(){
//        this.token = UUID.randomUUID()
//        expireDt = System.currentTimeMillis() + 3600 * 100 * 24;  // 하루간 유효한 값
//    }
}