package com.sindory.workenv.repository

import com.sindory.workenv.domain.entity.Auth
import com.sindory.workenv.domain.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository:JpaRepository<Member, Long>{
    fun findByAuth(auth: Auth): Member?
}
