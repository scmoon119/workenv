package com.sindory.workenv.service

import com.sindory.workenv.domain.entity.Auth
import com.sindory.workenv.repository.AuthRepository
import org.springframework.stereotype.Service
import java.util.*

open class BaseService (
    private val authRepository: AuthRepository,
){
    fun getUser(uid: String?): Auth {
        return authRepository.findByToken(UUID.fromString(uid)) ?: throw Exception("no such user.");
    }
}