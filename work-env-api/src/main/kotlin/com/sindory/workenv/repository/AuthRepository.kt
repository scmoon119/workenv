package com.sindory.workenv.repository

import com.sindory.workenv.domain.entity.Auth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AuthRepository:JpaRepository<Auth, String>{
    fun existsByUserId(userId:String?): Boolean
    fun findByUserId(userId:String?): Auth?
    fun findByToken(token: UUID): Auth?
}