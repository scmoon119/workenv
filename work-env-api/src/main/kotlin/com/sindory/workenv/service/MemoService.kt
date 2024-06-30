package com.sindory.workenv.service

import com.sindory.workenv.domain.entity.Memo
import com.sindory.workenv.dto.request.SaveMemoRequest
import com.sindory.workenv.dto.response.SaveResultResponse
import com.sindory.workenv.repository.AuthRepository
import com.sindory.workenv.repository.MemoRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class MemoService (
    val authRepository: AuthRepository,
    val memoRepository: MemoRepository,
){
    @Transactional
    fun saveMemo(request: SaveMemoRequest): ResponseEntity<SaveResultResponse> {
        var response = SaveResultResponse();

        // 사용자 확인.
        val loginedUser = authRepository.findByToken( UUID.fromString(request.uid));

        if (loginedUser == null){
            response.message = "no such user."
            return ResponseEntity.badRequest().body(response);
        }
        val memo = Memo( request.date, request.content, loginedUser );
        memoRepository.save(memo)

        response.message = "OK"
        return ResponseEntity.ok().body(response);

    }
}