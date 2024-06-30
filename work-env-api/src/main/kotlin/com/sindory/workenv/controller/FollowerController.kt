package com.sindory.workenv.controller

import com.sindory.workenv.dto.request.CreateFollowerRequest
import com.sindory.workenv.dto.request.DeleteFollowerRequest
import com.sindory.workenv.dto.request.SaveMemoRequest
import com.sindory.workenv.dto.response.DeleteResultResponse
import com.sindory.workenv.dto.response.GetFollowersResponse
import com.sindory.workenv.dto.response.SaveResultResponse
import com.sindory.workenv.service.FollowerService
import jakarta.servlet.http.Cookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class FollowerController(
    val followerService: FollowerService,
) {
    @GetMapping("/follower/followers")
    fun getFollowers(
//        @CookieValue(value = "uid", required = false) uid: String,
        @RequestParam uid: String,

        )
            : ResponseEntity<GetFollowersResponse> {
        return  followerService.getFollowers(uid)
    }

    @PutMapping("/follower")
    fun createFollower(
        @RequestBody request: CreateFollowerRequest,
        )
            : ResponseEntity<SaveResultResponse> {
        return followerService.createFollower(request)
    }

    @DeleteMapping("/follower/{followerId}/{uid}")
    fun deleteFollower(
        @PathVariable followerId: Long,
        @PathVariable uid: String,
    ): ResponseEntity<DeleteResultResponse>?{
        return followerService.deleteFollower(followerId, uid)
    }

    @PostMapping("/follower")
    fun updateFollower(
        @RequestBody request: CreateFollowerRequest,
//        @CookieValue(value = "uid", required = false) uidCookie: Cookie,
        )
            : ResponseEntity<SaveResultResponse> {
        return followerService.updateFollower(request)
    }

}