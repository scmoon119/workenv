package com.sindory.workenv.config

import com.sindory.workenv.interceptor.AuthInterceptor
import com.sindory.workenv.repository.AuthRepository
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfiguration(
    val authRepository: AuthRepository,
    ) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {

//        registry.addInterceptor(AuthInterceptor(authRepository))
//            .order(1)
//            .addPathPatterns("/memo/**")
//            .addPathPatterns("/task/**")
//            .addPathPatterns("/user/**")
//            .excludePathPatterns("/page/login/**")
        super.addInterceptors(registry)
    }
}