package com.sindory.workenv.service

import com.sindory.workenv.domain.entity.Auth
import com.sindory.workenv.domain.entity.Follower
import com.sindory.workenv.domain.entity.Memo
import com.sindory.workenv.domain.entity.Task
import com.sindory.workenv.dto.request.CreateFollowerRequest
import com.sindory.workenv.dto.request.DeleteFollowerRequest
import com.sindory.workenv.dto.response.*
import com.sindory.workenv.repository.AuthRepository
import com.sindory.workenv.repository.FollowerRepository
import com.sindory.workenv.repository.PerformanceLogRepository
import jakarta.servlet.http.Cookie
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class FollowerService(
    val authRepository: AuthRepository,
    val followerRepository: FollowerRepository,
    val performanceLogRepository: PerformanceLogRepository
    ){
    val log = KotlinLogging.logger {}
    fun getUser(uid: String?): Auth {
        return authRepository.findByToken(UUID.fromString(uid)) ?: throw Exception("no such user.");
    }

    fun getFollowers(uid: String): ResponseEntity<GetFollowersResponse> {
        val response = GetFollowersResponse()

        log.info ( "uid: ${uid}" )
        val loginedUser = getUser(uid);
        loginedUser.userId?.let { log.info("userId: ${it}") }
        val followerList = followerRepository.findByAuth(loginedUser)

        response.message = "OK"
        log.info("followerList.size: ${followerList?.size}")

        log.info("followerList: ${followerList}")
        if (followerList != null) {
            var followerListResponse: List<FollowerResponse>? = emptyList()
            for (follower in followerList) {
                val followerResponse: FollowerResponse = FollowerResponse(
                    followerId = follower.Id,
                    nickName = follower.nickName,
                    description = follower.description,
                )
                if (followerListResponse != null) {
                    followerListResponse = followerListResponse + followerResponse
                }
            }
            response.followerList = followerListResponse
        }
        return ResponseEntity.ok().body(response)
    }

    fun createFollower(request: CreateFollowerRequest): ResponseEntity<SaveResultResponse> {
        var response = SaveResultResponse()

        val loginedUser = getUser(request.uid);
        val follower = Follower(
            nickName = request.nickName,
            description = request.description,
            auth = loginedUser,
        )
        followerRepository.save(follower)

        response.message = "OK"
        return ResponseEntity.ok().body(response)

    }

    fun deleteFollower(followerId: Long, uid:String): ResponseEntity<DeleteResultResponse> {
        val deleteResultResponse = DeleteResultResponse()
        val follower = followerRepository.findFollowerById(followerId)
        if(follower == null){
            deleteResultResponse.message = "no such follower(${followerId})."
            return ResponseEntity.ok().body(deleteResultResponse)
        }
        // follower의 owner 와 uid 가 같은지 확인
        val loginedUser = getUser(uid);
        if((follower.auth != loginedUser)){
            deleteResultResponse.message = "you are not owner of this follower."
            return ResponseEntity.ok().body(deleteResultResponse)
        }

        // performanceLog 에 follower 로 연결된 log 가 존재하는 지 확인
        val performanceLogList = performanceLogRepository.findPerformanceLogByFollower(follower)
        if(!performanceLogList.isNullOrEmpty()){
            // performanceLog 가 존재하는 최초, 최후 날짜를 구한다.
            val firstDate = performanceLogList.minByOrNull { it.date }?.date
            val lastDate = performanceLogList.maxByOrNull { it.date }?.date

            deleteResultResponse.message =  "구성원을 표현하는 로그가 존재합니다. 먼저 로그를 삭제해 주세요.(${performanceLogList?.size ?: 0}건)\n최초:${firstDate} 최후 ${lastDate}"
            return ResponseEntity.ok().body(deleteResultResponse)
        }

        try{
            followerRepository.delete(follower)
        }catch (e: Exception){
            deleteResultResponse.message = e.message
            log.info("delete follower error: ${e.message}")
            return ResponseEntity.ok().body(deleteResultResponse)
        }
        deleteResultResponse.message = "OK"
        return ResponseEntity.ok().body(deleteResultResponse)

    }

    fun updateFollower(request: CreateFollowerRequest): ResponseEntity<SaveResultResponse> {

        val response = SaveResultResponse()

        val loginedUser = getUser(request.uid);

        followerRepository.findFollowerById(request.followerId!!)?.let { follower ->
            if(follower.auth != loginedUser){
                response.message = "you are not owner of this follower."
                return ResponseEntity.ok().body(response)
            }
            if(follower.nickName == request.nickName && follower.description == request.description){
                response.message = "no change."
                return ResponseEntity.ok().body(response)
            }
            follower.nickName = request.nickName
            follower.description = request.description
            followerRepository.save(follower)
            response.message = "OK"
        } ?: run {
            response.message = "no such follower(${request.followerId})."
        }

        return ResponseEntity.ok().body(response)
    }
}

