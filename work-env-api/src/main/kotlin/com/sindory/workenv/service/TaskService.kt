package com.sindory.workenv.service

import com.sindory.workenv.domain.InitTask
import com.sindory.workenv.domain.entity.*
import com.sindory.workenv.dto.response.MemoResponse
import com.sindory.workenv.dto.response.TaskResponse
import com.sindory.workenv.dto.WEDate
import com.sindory.workenv.dto.request.*
import com.sindory.workenv.dto.response.CreateTaskResponse
import com.sindory.workenv.dto.response.DeleteResultResponse
import com.sindory.workenv.dto.response.SaveResultResponse
import com.sindory.workenv.dto.response.TaskListResponse
import com.sindory.workenv.repository.AuthRepository
import com.sindory.workenv.repository.MemoRepository
import com.sindory.workenv.repository.TaskQuerydslRepository
import com.sindory.workenv.repository.TaskRepository
import jakarta.transaction.Transactional
import mu.KotlinLogging
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*


@Service
class TaskService (
    val authRepository: AuthRepository,
    val memoRepository: MemoRepository,
    val taskRepository: TaskRepository,
    val taskQuerydslRepository: TaskQuerydslRepository,
){
    val log = KotlinLogging.logger {}
    public val MAX_UNDONE_PRIORITY = 10000;

    fun getUser(uid: String?):Auth{
        return authRepository.findByToken(UUID.fromString(uid)) ?: throw Exception("no such user.");
    }

    @Transactional
    fun createTask(request: CreateTaskRequest): ResponseEntity<CreateTaskResponse> {
        var response = CreateTaskResponse()

        val loginedUser = getUser(request.uid);
        val memo = Memo( request.date, "", loginedUser )
        memoRepository.save(memo)

        var initTask = getInitTask(loginedUser, memo)

        val task = Task(
            initTask.importance,
            initTask.priority,
            initTask.content,
            loginedUser,
            memo,
        )
        taskRepository.save(task)

        response.message = "OK"
        response.taskId = task.taskId
        return ResponseEntity.ok().body(response)

    }

    private fun getInitTask(user: Auth, memo: Memo): InitTask {
        var initTask = InitTask()
        val task = taskQuerydslRepository.findMaxUndonePriority(user, memo, Importance.B)
        if(task != null){
            initTask.priority = task.priority + 1
        }
        return initTask
    }

    fun getTaskList(date:String, uid:String): ResponseEntity<TaskListResponse> {
        // 아래 내용에 DB 오류가 있으면 badRequest 와 callstack 을 body 에 return 하게 한다. (response.message 에 callstack 을 넣는다.)
        val response = TaskListResponse()
        val loginedUser = getUser(uid);

        var memo = memoRepository.findByDate(date)
        if(memo == null){
            memo = Memo(date, "")
            memoRepository.save(memo)
        }

        log.info { "memo: " + memo.date   }
        log.info { "loginedUser: " + loginedUser.userId }

        val findByTaskList = taskRepository.findByAuthAndMemo(
            loginedUser,
            memo,
            Sort.by("Importance").and(Sort.by("priority"))
            )

        response.message = "OK"
        response.memo = MemoResponse(memo.content)

        if (findByTaskList != null) {
            var taskList:List<TaskResponse>? = emptyList()
            for (task in findByTaskList) {
                val taskResponse: TaskResponse = TaskResponse(
                    task.importance,
                    task.priority,
                    task.content,
                    task.status.toString(),
                    task.taskId,
                )
                if (taskList != null) {
                    taskList = taskList + taskResponse
                }
            }
            response.taskList = taskList
        }else{
            response.taskList = null
        }

        return ResponseEntity.ok().body(response)
    }

    @Transactional
    fun updateTasks(request: UpdateTaskRequest, uid: String?): ResponseEntity<SaveResultResponse>? {
        var response = SaveResultResponse()

        val loginedUser = getUser(uid);

        for(task in request.tasks){
        }

        return ResponseEntity.ok().body(response)
    }

    fun updateTaskByContent(request: UpdateTaskByContentRequest): ResponseEntity<SaveResultResponse>? {
        var response = SaveResultResponse()
        var findByTaskId = taskRepository.findByTaskId(request.id);

        if (findByTaskId == null){
            response.message = "no such task."
            return ResponseEntity.badRequest().body(response)
        }

        findByTaskId.content = request.content;
        taskRepository.save(findByTaskId);

        response.message = "OK"
        return ResponseEntity.ok().body(response)

    }

    @Transactional
    fun deleteTask(taskId: Long, uid: String?): ResponseEntity<DeleteResultResponse>? {
        var response = DeleteResultResponse()

        var findByTaskId = taskRepository.findByTaskId(taskId);
        if (findByTaskId == null){
            response.message = "no such task."
            return ResponseEntity.badRequest().body(response)
        }
        val refPriority:Int = findByTaskId.priority;
        val refImportance:Importance = findByTaskId.importance;
        val refMemberId = findByTaskId.auth?.memberId;
        val refStatus = findByTaskId.status;
        val refDate = findByTaskId.memo?.date;
        try {
            taskRepository.delete(findByTaskId);

            if(isStatusUndone(refStatus)){
                val findTasksToUpdatePrioritys =
                    taskQuerydslRepository.findUndoneTasksToUpdatePriority(refMemberId, refDate, refImportance, refPriority);
                if(findTasksToUpdatePrioritys.isNotEmpty()){
                    findTasksToUpdatePrioritys.forEach { it.priority -= 1}
                    findTasksToUpdatePrioritys.forEach { taskRepository.save(it)}
                }
            }

         }catch(e:Exception){
            response.message = "error:" + e.toString();
            return ResponseEntity.ok().body(response)
        }

        response.message = "OK"
        return ResponseEntity.ok().body(response)
    }

    fun findMaxPriority(status:TaskStatus?, user: Auth?, memo: Memo?, importance: Importance): Task?{
        if(isStatusUndone(status))
            return taskQuerydslRepository.findMaxUndonePriority(user, memo, importance)
        else
            return taskQuerydslRepository.findMaxDonePriority(user, memo, importance)
    }

    fun findTasksToUpdatePrioritys(status:TaskStatus?, user: Auth?, memo: Memo?, importance: Importance, refPriority: Int): List<Task> {
        if(isStatusUndone(status))
            return taskQuerydslRepository.findUndoneTasksToUpdatePriority(user?.memberId, memo?.date, importance, refPriority)
        else
            return listOf()
    }

    fun updateTaskByImportance(request: UpdateTaskByImportanceRequest): ResponseEntity<SaveResultResponse>? {
        val loginedUser = getUser(request.uid);
        var findByTaskId = taskRepository.findByTaskId(request.id)
            ?: return ResponseEntity.badRequest().body(SaveResultResponse("no such task."));

        if (findByTaskId.importance == request.importance){
            return ResponseEntity.badRequest().body(SaveResultResponse("same importance."))
        }

        // priority 를 재조정한다. 만약 undone 이면 undone max +1 로 하고 done 이면  done max +1 로 한다.
        val refImportance = findByTaskId.importance;
        val refPriority = findByTaskId.priority;

        findByTaskId.importance = request.importance;
        val maxPriorityTask = findMaxPriority(findByTaskId.status, loginedUser, findByTaskId.memo, request.importance);
        if (maxPriorityTask == null) {
            findByTaskId.priority = 0;
        }else{
            findByTaskId.priority = maxPriorityTask.priority + 1;
        }

        try {
            taskRepository.save(findByTaskId);

            val findTasksToUpdatePrioritys =taskQuerydslRepository.findUndoneTasksToUpdatePriority(loginedUser.memberId,
                findByTaskId.memo?.date, refImportance, refPriority);

            if (findTasksToUpdatePrioritys.isNotEmpty()) {
                findTasksToUpdatePrioritys.forEach { it.priority -= 1 }
                findTasksToUpdatePrioritys.forEach { taskRepository.save(it) }
            }
        }catch(e:Exception){
            return ResponseEntity.badRequest().body(SaveResultResponse("error:" + e.toString()))
        }
        return ResponseEntity.ok().body(SaveResultResponse("OK"));
    }

    // '2024-01-11' 형식의 date 를 받아서 year 를 int 로 되돌려 주는 함수. 만약 date 가 빈 문자열이면 현재 년도를 되돌려 준다.
    fun getYear(date: String): Int {
        if (date == ""){
            val calendar = Calendar.getInstance()
            return calendar.get(Calendar.YEAR)
        }
        return date.substring(0,4).toInt()
    }
    // getMonth 도 getYear 처럼 만든다.
    fun getMonth(date: String): Int {
        if (date == ""){
            val calendar = Calendar.getInstance()
            return calendar.get(Calendar.MONTH) + 1
        }
        return date.substring(5,7).toInt()
    }
    // getDay 도 getYear 처럼 만든다.
    fun getDay(date: String): Int {
        if (date == ""){
            val calendar = Calendar.getInstance()
            return calendar.get(Calendar.DATE)
        }
        return date.substring(8,10).toInt()
    }


    fun getNDayLater(date: String, n: Int): String {
        val weDate = WEDate(getYear(date), getMonth(date), getDay(date))
        val calendar = Calendar.getInstance()
        calendar.set(weDate.year, weDate.month-1, weDate.day)
        calendar.add(Calendar.DATE, n)

        val weDateLater = WEDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE))
        val year = weDateLater.year
        val month = weDateLater.month
        val day = weDateLater.day
        val yearLater = year.toString()
        val monthLater = if (month < 10) "0$month" else month.toString()
        val dayLater = if (day < 10) "0$day" else day.toString()

        return "$yearLater-$monthLater-$dayLater"
    }


    fun CopyPostponeTask(task: Task, dayToPostpone: Int): Task {
        val date = task.memo?.date ?: ""
        val newDate = getNDayLater(date, dayToPostpone)
        var findMemoByNewDate = memoRepository.findByDate(newDate)

        if(findMemoByNewDate == null){
            findMemoByNewDate = Memo(newDate, "", task.auth)
            memoRepository.save(findMemoByNewDate)
        }

        var initTask = getInitTask(task.auth!!, findMemoByNewDate)

        val newTask = Task(
            initTask.importance,
            initTask.priority,
            task.content,
            task.auth,
            findMemoByNewDate,
        )
        taskRepository.save(newTask)

        return newTask
    }

    fun isStatusDone(status: TaskStatus?) = !isStatusUndone(status)
    fun isStatusUndone(status: TaskStatus?) = status == TaskStatus.TODO || status == TaskStatus.DOING

    // 아래 함수를 if 대신 when 절로 변경한다. (statusOrigin, status 를 받아서 statusOrigin 이 DONE 이고 status 가 CANCEL, DELEGATE, POSTPONE 중 하나이면 true 를 리턴한다.)
    fun isStatusChangeToDone(statusOrigin: TaskStatus?, status: TaskStatus) =
        when(statusOrigin) {
            in listOf(TaskStatus.TODO, TaskStatus.DOING) -> {
                when(status){
                    TaskStatus.CANCEL, TaskStatus.DELEGATE, TaskStatus.POSTPONE, TaskStatus.DONE ->  true
                    else -> false
                }
            }
            else -> {
                false
            }
        }

    fun isStatusChangeToUndone(statusOrigin: TaskStatus?, status: TaskStatus) =
        when(statusOrigin) {
            TaskStatus.CANCEL, TaskStatus.DELEGATE, TaskStatus.POSTPONE, TaskStatus.DONE -> {
                when(status){
                    TaskStatus.TODO, TaskStatus.DOING ->  true
                    else -> false
                }
            }
            else -> {
                false
            }
        }

    fun getDelegatedUserString(request: UpdateTaskByStatusRequest): String {
        return if (request.status == TaskStatus.DELEGATE && request.deligatedUser.isNotEmpty()){
            " (위임: " + request.deligatedUser + ")"
        }else{
            ""
        }
    }

    @Transactional
    fun updateTaskByStatus(request: UpdateTaskByStatusRequest): ResponseEntity<SaveResultResponse>? {
        // updateTaskByStatus   전체를 try 로 묶고 exception 이 발생하면 badRequest 를 리턴하도록 한다.
        try {
            val loginedUser = getUser(request.uid);
            var findByTaskId = taskRepository.findByTaskId(request.id)
                ?: return ResponseEntity.badRequest().body(SaveResultResponse("no such task."));

            if (findByTaskId.status == request.status) {
                return ResponseEntity.badRequest().body(SaveResultResponse("same status."))
            }
            // findByTaskId 의 status 를 request.status 로 변경한다.
            val statusOrigin = findByTaskId.status;
            val priorityOrigin = findByTaskId.priority;

            // 진행 중인 status 에서 완료 status 로 변경될 때
            if (isStatusChangeToDone(statusOrigin, request.status)) {
                // status 변경하고,
                findByTaskId.status = request.status;
                // 완료된 status 에 하나가 추가되었으니 priority 를 10000 이상으로 재 조정한다.
                val maxPriorityTask = taskQuerydslRepository.findMaxDonePriority(loginedUser, findByTaskId.memo, findByTaskId.importance);
                if (maxPriorityTask == null ){
                    findByTaskId.priority = MAX_UNDONE_PRIORITY;
                }else{
                    findByTaskId.priority = maxPriorityTask.priority + 1;
                }
                // delegate 된 task 는 contents 에 위임한 사람 String 을 추가한다. funtion 으로 빼자.
                if (request.status == TaskStatus.DELEGATE && request.deligatedUser.isNotEmpty()){
                    findByTaskId.content = findByTaskId.content + " (위임: " + request.deligatedUser + ")"
                }
                taskRepository.save(findByTaskId);
                // 진행 중인 status 에서 하나가 빠졌으니 priority 를 재 조정한다.
                val findTasksToUpdatePrioritys =
                    taskQuerydslRepository.findUndoneTasksToUpdatePriority(
                        loginedUser.memberId,
                        findByTaskId.memo?.date ,
                        findByTaskId.importance,
                        priorityOrigin);

                if(findTasksToUpdatePrioritys.isNotEmpty()){
                    findTasksToUpdatePrioritys.forEach { it.priority -= 1}
                    findTasksToUpdatePrioritys.forEach { taskRepository.save(it)}
                }
                // status 가 postpone 이면 그만큰 연기한 날짜의 memo 가 없으면 만들고 같은 contents 의 task 를 만들어서 저장한다.
                if (request.status == TaskStatus.POSTPONE){
                    CopyPostponeTask(findByTaskId, request.dayToPostpone)
//                    taskRepository.save(newTask)
                }

                return ResponseEntity.ok().body(SaveResultResponse("OK"));
            // 완료된 status 에서 진행 중 status 로 변경될 때
            }else if (isStatusChangeToUndone(statusOrigin, request.status)) {
                // status 변경하고
                findByTaskId.status = request.status;
                // 진행 중인 status 에 하나가 추가되었으니 priority 를 재 조정한다.
                val maxPriorityTask = taskQuerydslRepository.findMaxUndonePriority(loginedUser, findByTaskId.memo, findByTaskId.importance);
                if (maxPriorityTask == null ){
                    findByTaskId.priority = 0;
                }else{
                    findByTaskId.priority = maxPriorityTask.priority + 1;
                }
                findByTaskId.content = findByTaskId.content +  getDelegatedUserString(request)
                taskRepository.save(findByTaskId);
                // undone priority 는 조정할 필요가 없다. 내벼려 둔다.


                return ResponseEntity.ok().body(SaveResultResponse("OK"));
            }else{
                findByTaskId.status = request.status;
                findByTaskId.content = findByTaskId.content +  getDelegatedUserString(request)

                taskRepository.save(findByTaskId);
                return ResponseEntity.ok().body(SaveResultResponse("OK"));
            }

        }catch(e:Exception){
            println(e.printStackTrace())
            return ResponseEntity.badRequest().body(SaveResultResponse("error:" + e.printStackTrace()))
        }
    }

//    @Transactional
    fun moveNorth(request: MoveNorthRequest): ResponseEntity<SaveResultResponse>? {
        val loginedUser = getUser(request.uid);
        var findByTaskId = taskRepository.findByTaskId(request.id)
            ?: return ResponseEntity.badRequest().body(SaveResultResponse("no such task."));

        if (findByTaskId.priority <= 0 ){
            return ResponseEntity.badRequest().body(SaveResultResponse("can't move."))
        }

        val findTaskToMove = taskQuerydslRepository.findPrevPriority(
            findByTaskId.auth, findByTaskId.memo, findByTaskId.importance, findByTaskId.priority
        ) ?: return ResponseEntity.badRequest().body(SaveResultResponse("can't move."))

        val tempToMove = findTaskToMove.priority
        val tempTask = findByTaskId.priority

        findTaskToMove.priority = -1000;
        taskRepository.save(findTaskToMove)
        findByTaskId.priority = tempToMove;
        taskRepository.save(findByTaskId)
        findTaskToMove.priority = tempTask;
        taskRepository.save(findTaskToMove)

        return ResponseEntity.ok().body(SaveResultResponse("OK"));
    }

    fun moveSouth(request: MoveSouthRequest): ResponseEntity<SaveResultResponse>? {
        val loginedUser = getUser(request.uid);

        var findByTaskId = taskRepository.findByTaskId(request.id)
            ?: return ResponseEntity.badRequest().body(SaveResultResponse("no such task."));

        //priority가 제일 큰 task 를 구함
        val maxPriorityTask = taskQuerydslRepository.findMaxUndonePriority(findByTaskId.auth, findByTaskId.memo, findByTaskId.importance)
            ?: return ResponseEntity.badRequest().body(SaveResultResponse("there is no max priority(why?)"))

        if (findByTaskId.priority >= maxPriorityTask.priority ){
            return ResponseEntity.badRequest().body(SaveResultResponse("can't move. you are already at the bottom."))
        }

        val findTaskToMove = taskQuerydslRepository.findNextPriority(
            findByTaskId.auth, findByTaskId.memo, findByTaskId.importance, findByTaskId.priority
        ) ?: return ResponseEntity.badRequest().body(SaveResultResponse("can't move. get findTaksToMove failed."))

        val tempToMove = findTaskToMove.priority
        val tempTask = findByTaskId.priority

        findTaskToMove.priority = 50000;
        taskRepository.save(findTaskToMove)
        findByTaskId.priority = tempToMove;
        taskRepository.save(findByTaskId)
        findTaskToMove.priority = tempTask;
        taskRepository.save(findTaskToMove)

        return ResponseEntity.ok().body(SaveResultResponse("OK"));
    }
}
