package com.sindory.workenv.controller

import com.sindory.workenv.dto.request.CreateTaskRequest
import com.sindory.workenv.dto.request.SaveMemoRequest
import com.sindory.workenv.dto.response.SaveResultResponse
import com.sindory.workenv.service.MemoService
import jakarta.servlet.http.Cookie
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
//
//], allowCredentials = "true")

class MemoController (
    val memoService:MemoService
){
    @PostMapping("/memo/memo")
    fun createTask(
        @RequestBody request: SaveMemoRequest,
    )
            : ResponseEntity<SaveResultResponse> {
        return memoService.saveMemo(request)
    }

}