package com.sindory.workenv.domain.entity

import jakarta.persistence.*

@Entity
class Memo(
    @Id
    var date: String,

    @Column(length = 1024 * 1024)
    var content: String? = null,

    @ManyToOne
    @JoinColumn(name = "member_id")
    val auth: Auth? = null,
) {
}