package com.sindory.workenv.domain

import com.sindory.workenv.domain.entity.Auth
import com.sindory.workenv.domain.entity.Importance
import com.sindory.workenv.domain.entity.Memo
import com.sindory.workenv.domain.entity.TaskStatus
import jakarta.persistence.*
import org.jetbrains.annotations.NotNull

data class InitTask (
    val importance: Importance = Importance.B,
    var priority: Int = 0,
    val content:String = "",
    @Enumerated(EnumType.ORDINAL)
    val status: TaskStatus? = TaskStatus.TODO,
    )
{
}