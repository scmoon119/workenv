package com.sindory.workenv.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.jetbrains.annotations.NotNull

@Entity
class UserCreateRequest (
    @NotNull
    var userId: String,
    @NotNull
    var password:String,
    @NotNull
    var name:String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    ) {
    init {
        if (userId.isBlank() || password.isBlank() || name.isBlank()) {
            throw IllegalArgumentException("아이디/암호/이름은 비어 있을 수 없습니다")
        }
    }
}

