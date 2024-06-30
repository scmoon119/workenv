package com.sindory.workenv.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("http://localhost:3000")
        config.addAllowedOrigin("http://localhost:8081")
        config.addAllowedOrigin("http://localhost")
        config.addAllowedOrigin("http://15.164.86.58")
        config.addAllowedOrigin("http://www.sindory.pe.kr")
        config.addAllowedOrigin("http://sindory.pe.kr")
        config.addAllowedOrigin("http://api.sindory.pe.kr:8081")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        config.addExposedHeader("Set-Cookie")
        config.addExposedHeader("Custom-Header")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }
}