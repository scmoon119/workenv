package com.sindory.workenv.repository

import com.sindory.workenv.domain.entity.Memo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemoRepository:JpaRepository<Memo, String>{
    fun findByDate(date: String): Memo?
}