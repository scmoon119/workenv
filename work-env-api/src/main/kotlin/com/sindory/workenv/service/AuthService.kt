package com.sindory.workenv.service

import com.sindory.workenv.domain.entity.Auth
import com.sindory.workenv.domain.entity.UserCreateRequest
import com.sindory.workenv.dto.request.CraeteUserRequest
import com.sindory.workenv.dto.request.LoginRequest
import com.sindory.workenv.dto.response.LoginResponse
import com.sindory.workenv.dto.response.SaveResultResponse
import com.sindory.workenv.repository.AuthRepository
import com.sindory.workenv.repository.MemberRepository
import com.sindory.workenv.repository.UserCreateRequestRepository
import com.sindory.workenv.utils.SindoryProperties
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Service
class AuthService (
    private val authRepository: AuthRepository,
    private val memberRepository: MemberRepository,
    private val sindoryProperties: SindoryProperties,
    private val userCreateRequestRepository: UserCreateRequestRepository
){
    fun generateCookie(auth:Auth): ResponseCookie {
        return ResponseCookie.from("uid", auth.token.toString())
            .sameSite("None" )
            .secure(false)
            .httpOnly(false)
            .maxAge(sindoryProperties.server.expireSecond * 3600)
            .path("/")
            .build()
    }

    @Transactional
    fun loginUser(request: LoginRequest): ResponseEntity<LoginResponse>{
        val log = KotlinLogging.logger {}

        val findAuth: Auth? = authRepository.findByUserId(request.userId)
        if ((findAuth == null) || (findAuth.password != request.password)){
           val failResponse = LoginResponse( request.userId, "user not found.")
            return ResponseEntity.badRequest().body(failResponse)
        }
        val findMember = memberRepository.findByAuth(findAuth)
        if (findMember == null){
            val failResponse = LoginResponse(request.userId, "member not found.")
            return ResponseEntity.badRequest().body(failResponse)
        }

        findAuth.token = UUID.randomUUID()
        findAuth.expireDt = System.currentTimeMillis() + 3600 * 100 * sindoryProperties.server.expireSecond;  // 하루간 유효한 값

        authRepository.save(findAuth)

        val response = LoginResponse(
            request.userId,
            findMember.name,
            findAuth.token,
            sindoryProperties.server.expireSecond)
        response.message = "login OK"

        val cookie = generateCookie(findAuth)
        log.info("cookie: ${cookie.toString()}")

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(response)
    }

    fun createUser(request: CraeteUserRequest): ResponseEntity<SaveResultResponse> {
        var response = SaveResultResponse();

        val existByAuthId = authRepository.existsByUserId(request.userId)
        if (existByAuthId){
            response.message = "이미 존재하는 ID 입니다."
            return ResponseEntity.badRequest().body(response);
        }

        val existsById = userCreateRequestRepository.existsByUserId(request.userId)
        if (existsById){
            response.message = "이미 신청중인 ID 입니다."
            return ResponseEntity.badRequest().body(response);
        }
        //request 로 부터 userCreateRqeust 생성
        val newRequest = UserCreateRequest(request.userId, request.password, request.name)
        userCreateRequestRepository.save(newRequest)

        response.message = "사용자 생성 요청이 완료되었습니다. 관리자의 승인을 기다려주세요.";


        return ResponseEntity.ok().body(response);
    }
}