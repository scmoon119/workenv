package com.sindory.workenv.controller

import com.sindory.workenv.domain.entity.PerformanceLog
import com.sindory.workenv.dto.request.DeleteLogRequest
import com.sindory.workenv.dto.request.SaveLogRequest
import com.sindory.workenv.dto.request.SaveMemoRequest
import com.sindory.workenv.dto.response.GetFollowersResponse
import com.sindory.workenv.dto.response.GetPerformanceLogResponse
import com.sindory.workenv.dto.response.SaveResultResponse
import com.sindory.workenv.service.PerformanceLogService
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class PerformanceLogController (
    val performanceLogService: PerformanceLogService,
){
    val log = KotlinLogging.logger {}
    @GetMapping("/performance-log/logs/{date}")
    fun getPerformanceLogs(
        @PathVariable date:String,
        @RequestParam uid: String,

//        @CookieValue(value = "uid", required = false) uid: String,
    )
            : ResponseEntity<GetPerformanceLogResponse> {
        log.info { "saveLog[uid:${uid}],[date:${date}]" }

        return  performanceLogService.getPerformanceLogByDate(uid, date)
    }

    @GetMapping("/performance-log/logs/{fromDate}/{toDate}/{followerId}")
    fun getPerformanceLogs(
        @PathVariable fromDate:String,
        @PathVariable toDate:String,
        @PathVariable followerId:Long,
        @RequestParam uid: String,
//        @CookieValue(value = "uid", required = false) uid: String,
    )
            : ResponseEntity<GetPerformanceLogResponse> {
        log.info { "saveLog[uid:${uid}],[fromDate:${fromDate}][toDate:${toDate}][followerId:${followerId}]" }

        return  performanceLogService.getPerformanceLogByDateAndByFollower(uid, fromDate, toDate, followerId)
    }

    @PostMapping("/performance-log/delete-logs")
    fun saveLog(
        @RequestBody request: DeleteLogRequest,

//        @CookieValue(value = "uid", required = false) uid: String,
    )
            : ResponseEntity<SaveResultResponse> {
        return performanceLogService.deleteLogs(request)
    }



    @PostMapping("/performance-log/save-log")
    fun saveLog(
        @RequestBody request: SaveLogRequest,

    )
            : ResponseEntity<SaveResultResponse> {
        return performanceLogService.savePerformanceLog(request)
    }

}