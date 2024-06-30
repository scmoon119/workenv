package com.sindory.workenv.controller

import com.sindory.workenv.dto.request.CraeteUserRequest
import com.sindory.workenv.dto.request.LoginRequest
import com.sindory.workenv.dto.response.CreateAuthResponse
import com.sindory.workenv.dto.response.LoginResponse
import com.sindory.workenv.dto.response.SaveResultResponse
import com.sindory.workenv.service.AuthService
import jakarta.servlet.http.Cookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
//@CrossOrigin(origins = [
//    "http://localhost:3000/",
//    "http://localhost:8081/",
//    "http://localhost",
//    "http://15.164.86.58/",
//    "http://www.sindory.pe.kr/",
//    "http://sindory.pe.kr/",
//    "http://api.sindory.pe.kr:8081/"
//], allowCredentials = "true", exposedHeaders = ["Set-Cookie"], allowedHeaders = ["*"])
private class AuthController(
    val authService: AuthService,
    )
{
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse>{
        return authService.loginUser(request);
    }

    @PutMapping("/user")
    // 사용자 생성을 신청하는 controller method
    fun createUser(
        @RequestBody request: CraeteUserRequest,
    ): ResponseEntity<SaveResultResponse>{
        return authService.createUser(request)
    }

}