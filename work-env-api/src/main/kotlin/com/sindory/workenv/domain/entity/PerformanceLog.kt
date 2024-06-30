package com.sindory.workenv.domain.entity

import jakarta.persistence.*
import org.jetbrains.annotations.NotNull

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["follower_id", "date"])])

class PerformanceLog (

    @ManyToOne
    @JoinColumn(name = "follower_id")
    val follower: Follower? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val Id : Long?=null,

    @NotNull
    var date: String,

    @NotNull
    @Column(length = 1024 * 16)
    var content: String,

    ){
}