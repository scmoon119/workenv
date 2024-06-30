package com.sindory.workenv.dto.response

data class TaskListResponse(
    var taskList:List<TaskResponse>? = null,
    var memo: MemoResponse? = null,
): SaveResultResponse(){
}