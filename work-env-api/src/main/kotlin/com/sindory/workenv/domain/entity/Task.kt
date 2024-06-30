package com.sindory.workenv.domain.entity

import jakarta.persistence.*
import org.jetbrains.annotations.NotNull

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["importance", "priority", "member_id", "date"])])

class Task(
    @NotNull
    @Column(name = "importance")
    @Enumerated(EnumType.ORDINAL)
    var importance: Importance,
    @NotNull
    @Column(name = "priority")
    var priority: Int,

    @NotNull
    var content:String,


    @ManyToOne
    @JoinColumn(name = "member_id")
    val auth: Auth? = null,

    @ManyToOne
    @JoinColumn(name = "date")
    var memo: Memo? = null,

    @Enumerated(EnumType.ORDINAL)
    var status:TaskStatus? = TaskStatus.TODO,


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val taskId : Long?=null,

    @Version
    var version: Long? = null

    ) {

//    init {
//        if(name.isBlank()){
//            throw IllegalArgumentException("이름은 비어 있을 수 없습니다")
//        }
//    }
}