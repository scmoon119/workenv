package com.sindory.workenv.repository

import com.sindory.workenv.domain.entity.Auth
import com.sindory.workenv.domain.entity.Task
import com.sindory.workenv.domain.entity.UserCreateRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserCreateRequestRepository: JpaRepository<UserCreateRequest, Long> {
    fun save(userCreateRequest: UserCreateRequest)
    fun findByUserId(userId:String?): UserCreateRequest?
    fun existsByUserId(userId: String?): Boolean

}