package com.sindory.workenv.repository

import com.sindory.workenv.domain.InitTask
import com.sindory.workenv.domain.entity.Auth
import com.sindory.workenv.domain.entity.Importance
import com.sindory.workenv.domain.entity.Memo
import com.sindory.workenv.domain.entity.Task
import jakarta.annotation.Priority
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository:JpaRepository<Task, Long>{
    fun findByTaskId(taskId: Long): Task?
    fun findByAuthAndMemo(user: Auth, memo: Memo, sort:Sort): List<Task>?
    fun save(task: Task)

}