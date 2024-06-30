package com.sindory.workenv.repository

import com.sindory.workenv.domain.entity.Auth
import com.sindory.workenv.domain.entity.Follower
import com.sindory.workenv.domain.entity.Memo
import com.sindory.workenv.domain.entity.Task
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface FollowerRepository: JpaRepository<Follower, Long> {
    fun findByAuth(auth: Auth): List<Follower>?
    fun findFollowerById(followerId: Long): Follower?
}
