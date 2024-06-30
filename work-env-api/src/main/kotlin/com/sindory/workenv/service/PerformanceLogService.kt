package com.sindory.workenv.service

import com.sindory.workenv.domain.entity.Auth
import com.sindory.workenv.domain.entity.PerformanceLog
import com.sindory.workenv.dto.request.DeleteLogRequest
import com.sindory.workenv.dto.request.SaveLogRequest
import com.sindory.workenv.dto.response.GetPerformanceLogResponse
import com.sindory.workenv.dto.response.PerformanceLogResponse
import com.sindory.workenv.dto.response.SaveResultResponse
import com.sindory.workenv.repository.AuthRepository
import com.sindory.workenv.repository.FollowerRepository
import com.sindory.workenv.repository.PerformanceLogRepository
import jakarta.transaction.Transactional
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class PerformanceLogService(
    val authRepository: AuthRepository,
    val followerRepository: FollowerRepository,
    val performanceLogRepository: PerformanceLogRepository,
    private val springDataWebProperties: SpringDataWebProperties,
){
    val log = KotlinLogging.logger {}
    fun getUser(uid: String?): Auth {
        return authRepository.findByToken(UUID.fromString(uid)) ?: throw Exception("no such user.");
    }

    fun getPerformanceLogByDate(uid:String, date: String): ResponseEntity<GetPerformanceLogResponse> {
        var response = GetPerformanceLogResponse()
        // uid 체크
        val loginedUser = getUser(uid);
        if(loginedUser == null){
            response.message = "no such user."
            return ResponseEntity.ok().body(response)
        }
        // follower 여부 체크
        val followers = followerRepository.findByAuth(loginedUser)
        if(followers == null){
            response.message = "no followers."
            return ResponseEntity.ok().body(response)
        }
        val logs =
            performanceLogRepository.findPerformanceLogByDateAndFollowerIn(date, followers)

        if (logs != null) {
            var logListResponse: List<PerformanceLogResponse>? = emptyList()
            for (log in logs) {
                val performanceLogResponse: PerformanceLogResponse = PerformanceLogResponse(
                    performanceLogId = log.Id,
                    content = log.content,
                    followerId = log.follower?.Id,
                    followerName = log.follower?.nickName,
                )
                if (logListResponse != null) {
                    logListResponse = logListResponse + performanceLogResponse
                }
            }
            response.performanceLogs = logListResponse
        }
        response.message = "OK"

        return ResponseEntity.ok().body(response)
    }

    fun savePerformanceLog( request: SaveLogRequest): ResponseEntity<SaveResultResponse> {
        log.info("savePerformanceLog : ${request.log}, ${request.date}, ${request.followerId}")

        var response = SaveResultResponse()
        // followerId 체크
        val loginedUser = getUser(request.uid);
        if(loginedUser == null){
            response.message = "no such user."
            return ResponseEntity.ok().body(response)
        }

        // request 체크
        if(request.date == null || request.log == null || request.followerId == null){
            response.message = "invalid request."
            return ResponseEntity.ok().body(response)
        }

        // follower 여부 체크
        val follower = followerRepository.findFollowerById(request.followerId)
        if(follower == null){
            response.message = "no such follower."
            return ResponseEntity.ok().body(response)
        }
        // follower uid 체크
        if(follower.auth != loginedUser){
            response.message = "no such follower."
            return ResponseEntity.ok().body(response)
        }

        // date 와 follower 로 performancelog 를 구해옴
        val log = performanceLogRepository.findPerformanceLogByDateAndFollower(request.date, follower)
        if (log != null) {
            log.content = request.log
            performanceLogRepository.save(log)
            response.message = "OK"
            return ResponseEntity.ok().body(response)
        }
        if (request.logId == null){
            PerformanceLog(
                content = request.log,
                follower = follower,
                date = request.date,
            ).let {
                performanceLogRepository.save(it)
            }
            response.message = "OK"
            return ResponseEntity.ok().body(response)
        }
        response.message = "Something wrong."
        return ResponseEntity.ok().body(response);
    }

    fun getPerformanceLogByDateAndByFollower(
        uid: String,
        fromDate: String,
        toDate: String,
        followerId: Long
    ): ResponseEntity<GetPerformanceLogResponse> {
        var response = GetPerformanceLogResponse()

        //  uid 체크
        val loginedUser = getUser(uid);
        if(loginedUser == null){
            response.message = "no such user."
            return ResponseEntity.ok().body(response)
        }
        // followerId 체크
        val follower = followerRepository.findFollowerById(followerId)
        if(follower == null){
            response.message = "no such follower."
            return ResponseEntity.ok().body(response)
        }
        // follower uid 체크
        if(follower.auth != loginedUser){
            response.message = "no such follower."
            return ResponseEntity.ok().body(response)
        }

        val logs = performanceLogRepository.findPerformanceLogsByFollowerAndDateBetween(follower, fromDate, toDate);
        log.info { "logs : ${logs?.size}"}

        if (logs != null) {
            var logListResponse: List<PerformanceLogResponse>? = emptyList()
            for (performanceLog in logs) {
                val performanceLogResponse: PerformanceLogResponse = PerformanceLogResponse(
                    performanceLogId = performanceLog.Id,
                    content = performanceLog.content,
                    followerId = performanceLog.follower?.Id,
                    followerName = performanceLog.follower?.nickName,
                    date = performanceLog.date,
                )
                if (logListResponse != null) {
                    logListResponse = logListResponse + performanceLogResponse
                }
            }
            response.performanceLogs = logListResponse
        }
        response.performanceLogs?.sortedBy { it.date }
        response.message = "OK";
        return ResponseEntity.ok().body(response)
    }

    @Transactional
    fun deleteLogs(request: DeleteLogRequest): ResponseEntity<SaveResultResponse> {
        val response = SaveResultResponse();
        // uid 체크
        val loginedUser = getUser(request.uid);
        if(loginedUser == null){
            response.message = "no such user."
            return ResponseEntity.ok().body(response)
        }
        // followerId 체크
        if(request.followerId == null){
            response.message = "invalid request."
            return ResponseEntity.ok().body(response)
        }
        val follower = followerRepository.findFollowerById(request.followerId!!)
        if(follower == null){
            response.message = "no such follower."
            return ResponseEntity.ok().body(response)
        }
        // follower uid 체크
        if(follower.auth != loginedUser){
            response.message = "no such follower."
            return ResponseEntity.ok().body(response)
        }

        if(request.performanceLogIds == null || request.performanceLogIds!!.isEmpty()){
            response.message = "invalid request(no performanceLogIds)."
            return ResponseEntity.ok().body(response)
        }

        request.performanceLogIds!!.forEach {
            performanceLogRepository.deletePerformanceLogById(it)
        }

        response.message = "OK"
        return ResponseEntity.ok().body(response)

    }
}