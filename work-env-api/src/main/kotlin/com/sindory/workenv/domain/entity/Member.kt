package com.sindory.workenv.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne

@Entity
class Member(
    var name:String,

    @OneToOne
    @JoinColumn(name = "member_id")
    val auth: Auth? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val Id : Long?=null,

    ) {
    init {
        if(name.isBlank()){
            throw IllegalArgumentException("이름은 비어 있을 수 없습니다")
        }
    }
}