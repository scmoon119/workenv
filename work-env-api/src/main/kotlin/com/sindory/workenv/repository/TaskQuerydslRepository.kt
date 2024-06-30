package com.sindory.workenv.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.sindory.workenv.domain.entity.Auth
import com.sindory.workenv.domain.entity.Importance
import com.sindory.workenv.domain.entity.Memo
import com.sindory.workenv.domain.entity.QTask.task
import com.sindory.workenv.domain.entity.Task
import com.sindory.workenv.service.TaskService
import org.springframework.stereotype.Component

@Component
class TaskQuerydslRepository (
    private val queryFactory: JPAQueryFactory,
){
    fun findTasksToUpdatePriority(memberId: Long?, date: String?, importance: Importance?, refPriority: Int): List<Task> {
        return queryFactory
            .selectFrom(task)
            .where(
                task.auth.memberId.eq(memberId),
                task.memo.date.eq(date),
                task.importance.eq(importance),
                task.priority.gt(refPriority)
            )
            .orderBy(task.priority.asc())
            .fetch()
    }
    fun findTopTask(auth: Auth?, memo: Memo?, importance: Importance, priority: Int):Task? {
        return queryFactory
            .selectFrom(task)
            .where(
                task.auth.eq(auth),
                task.memo.eq(memo),
                task.importance.eq(importance),
                task.priority.eq(priority)
            )
            .fetchOne()
    }


    fun findMaxPriority(user: Auth?, memo: Memo?, importance: Importance): Task?{
        return queryFactory
            .selectFrom(task)
            .where(
                task.auth.eq(user),
                task.memo.eq(memo),
                task.importance.eq(importance),
            )
            .orderBy(task.priority.desc())
            .fetchFirst()
    }

    fun findPrevPriority(auth: Auth?, memo: Memo?, importance: Importance, priority: Int):Task?{
        return queryFactory
            .selectFrom(task)
            .where(
                task.auth.eq(auth),
                task.memo.eq(memo),
                task.importance.eq(importance),
                task.priority.lt(priority)
            )
            .orderBy(task.priority.desc())
            .fetchFirst()
    }

    //  특정 priority 보다 큰 최소의 priority를 가진 task를 찾는다.
    //  메모는 메모라기 보다 날짜의 의미가 있다.
    fun findNextPriority(auth: Auth?, memo: Memo?, importance: Importance, priority: Int):Task?{
        return queryFactory
            .selectFrom(task)
            .where(
                task.auth.eq(auth),
                task.memo.eq(memo),
                task.importance.eq(importance),
                task.priority.gt(priority)
            )
            .orderBy(task.priority.asc())
            .fetchFirst()
    }

    fun findMaxUndonePriority(user: Auth?, memo: Memo?, importance: Importance): Task? {
        return queryFactory
            .selectFrom(task)
            .where(
                task.auth.eq(user),
                task.memo.eq(memo),
                task.importance.eq(importance),
                task.priority.lt(10000) // 10000 이상의 priority는 따로 관리한다. (완료된 task)
            )
            .orderBy(task.priority.desc())
            .fetchFirst()

    }

    fun findMaxDonePriority(user: Auth?, memo: Memo?, importance: Importance): Task? {
        return queryFactory
            .selectFrom(task)
            .where(
                task.auth.eq(user),
                task.memo.eq(memo),
                task.importance.eq(importance),
                task.priority.goe( 10000) // 10000 이상의 priority
            )
            .orderBy(task.priority.desc())
            .fetchFirst()

    }

    fun findUndoneTasksToUpdatePriority(memberId: Long?, date: String?, importance: Importance?, priority: Int): List<Task> {
        return queryFactory
            .selectFrom(task)
            .where(
                task.auth.memberId.eq(memberId),
                task.memo.date.eq(date),
                task.importance.eq(importance),
                task.priority.gt(priority),
                task.priority.lt(10000)
            )
            .orderBy(task.priority.asc())
            .fetch()
    }

}