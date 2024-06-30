package com.sindory.workenv.interceptor

import com.sindory.workenv.repository.AuthRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.servlet.HandlerInterceptor
import java.util.*

@CrossOrigin(origins = [
    "http://localhost:3000/",
    "http://localhost:8081/",
    "http://localhost",
    "http://15.164.86.58/",
    "http://www.sindory.pe.kr/",
    "http://sindory.pe.kr/",
    "http://api.sindory.pe.kr:8081/"

], allowCredentials = "true")

class AuthInterceptor(
    val authRepository: AuthRepository,
    ) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {


        val log = KotlinLogging.logger {}
        log.info("request: ${request.requestURI}")

        //cookie 를 가져와서 log 에 출력한다.
        val cookies = request.cookies
        log.info("cookies: $cookies")
        cookies?.let {
            for (cookie in cookies) {
                log.info("cookie: ${cookie.name} ${cookie.value} in preHandle")
            }
        }

        if(cookies == null) {
            log.info("no cookies")
            return false
        }

        val uid = cookies?.find { it.name == "uid" }?.value
        log.info("uid: $uid")

        if(uid == null) {
            return false
        }

        if(authRepository == null){
            return false
        }
        val loginedUser = authRepository.findByToken(UUID.fromString(uid!!))
        if (loginedUser == null){
            log.info("no such user:$uid")
//            response.sendRedirect("/pages/login/login3")
            return false
        }
        return true;

    }
}