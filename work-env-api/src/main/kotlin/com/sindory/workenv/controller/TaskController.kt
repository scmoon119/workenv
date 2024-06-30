package com.sindory.workenv.controller

import com.sindory.workenv.dto.request.*
import com.sindory.workenv.dto.response.*
import com.sindory.workenv.service.TaskService
import jakarta.security.auth.message.callback.PrivateKeyCallback.SubjectKeyIDRequest
import jakarta.servlet.http.Cookie
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
//@CrossOrigin(origins = [
//    "http://localhost:3000/",
//    "http://localhost:8081/",
//    "http://localhost",
//    "http://15.164.86.58/",
//    "http://www.sindory.pe.kr/",
//    "http://sindory.pe.kr/",
//    "http://api.sindory.pe.kr:8081/"
//], allowCredentials = "true")

class TaskController (
    val taskService: TaskService,
) {
    val log = KotlinLogging.logger {}

    @PutMapping("/task/task")
    fun createTask(
        @RequestBody request: CreateTaskRequest,
    )
            : ResponseEntity<CreateTaskResponse> {
        return taskService.createTask(request)
    }

    @PostMapping("/task/taskByImportance")
    fun updateTask(
        @RequestBody request: UpdateTaskByImportanceRequest,
    ): ResponseEntity<SaveResultResponse>?{
        return taskService.updateTaskByImportance(request)
    }

    @PostMapping("/task/taskByStatus")
    fun updateTask(
        @RequestBody request: UpdateTaskByStatusRequest,
    ): ResponseEntity<SaveResultResponse>?{
        return taskService.updateTaskByStatus(request)
    }

    @PostMapping("/task/taskByContent")
    fun updateTask(
        @RequestBody request: UpdateTaskByContentRequest,
    ): ResponseEntity<SaveResultResponse>?{
        return taskService.updateTaskByContent(request)
    }

    @DeleteMapping("/task/task/{id}/{uid}")
    fun deleteTask(
        @PathVariable id: Long,
        @PathVariable uid: String,
    ): ResponseEntity<DeleteResultResponse>?{
        return taskService.deleteTask(id,uid)
    }


    @GetMapping("/task/tasks")
    fun getTaskList(
//        @RequestBody request: TaskListRequest,
        @RequestParam date: String,
        @RequestParam uid: String,
//        @CookieValue(value = "uid", required = false) uidCookie: Cookie,
    )
            : ResponseEntity<TaskListResponse> {
        log.info("uid : " + uid)
//        if(uidCookie == null){
//            val failResponse = TaskListResponse("cookie is null")
//            return ResponseEntity.badRequest().body(failResponse)
//        }

        return taskService.getTaskList(date, uid)
    }

    @PostMapping("/task/moveNorth")
    fun moveNorth(
        @RequestBody request: MoveNorthRequest,
    ): ResponseEntity<SaveResultResponse>?{
        return taskService.moveNorth(request )
    }

    // moveNorth 와 비슷한 로직으로 moveSouth 를 구현한다.
    @PostMapping("/task/moveSouth")
    fun moveSouth(
        @RequestBody request: MoveSouthRequest,
    ): ResponseEntity<SaveResultResponse>?{
        return taskService.moveSouth(request)
    }

}