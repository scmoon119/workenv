package com.sindory.workenv.domain.entity

import jakarta.persistence.*
import org.jetbrains.annotations.NotNull

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["nickName"])])

class Follower (
    @NotNull
    var nickName:String,
    var description:String,

    @ManyToOne
    @JoinColumn(name = "member_id")
    val auth: Auth? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val Id : Long?=null,

    ) {

}