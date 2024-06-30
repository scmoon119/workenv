package com.sindory.workenv.repository

import com.sindory.workenv.domain.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface PerformanceLogRepository: JpaRepository<PerformanceLog, Long> {
    fun findPerformanceLogByDate(date: String): List<PerformanceLog>?
    fun findPerformanceLogByDateAndFollowerIn(date: String, followers: List<Follower>): List<PerformanceLog>?
    fun findPerformanceLogByFollower(follower: Follower): List<PerformanceLog>?
    fun findPerformanceLogById(id: Long): PerformanceLog?

    fun findPerformanceLogByDateAndFollower(date: String, follower: Follower): PerformanceLog?

    //    fun findPerformanceLogsByFollowerAndDateWithin(follower: Follower, startDate: String, endDate: String): List<PerformanceLog>?
    fun findPerformanceLogsByFollowerAndDateBetween(
        follower: Follower,
        startDate: String,
        endDate: String
    ): List<PerformanceLog>?

//    fun deleteAllById(ids: List<Long>)
    fun deletePerformanceLogById(id: Long)
}
