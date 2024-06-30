package com.sindory.workenv.config

import jakarta.persistence.EntityManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QuerydslConfig(
    private val em: EntityManager,
) {
    @Bean
    fun querydsl(): com.querydsl.jpa.impl.JPAQueryFactory {
        return com.querydsl.jpa.impl.JPAQueryFactory(em)
    }
}