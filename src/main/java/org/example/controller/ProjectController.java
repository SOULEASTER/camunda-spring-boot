package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api
@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

    @Resource
    private TaskService taskService;
    @Resource
    private RuntimeService runtimeService;

    private static class ProjectParticipateRequestRecord {
        Long studentId;

        Long projectParticipateId;

        String taskId;

        public Long getProjectParticipateId() {
            return projectParticipateId;
        }

        public Long getStudentId() {
            return studentId;
        }

        public void setProjectParticipateId(Long projectParticipateId) {
            this.projectParticipateId = projectParticipateId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }
    }

    private static class UploadExtraInfoRecord {
        private String taskId;

        private String theUploadUrlOfExtraInfo;

        public String getTaskId() {
            return taskId;
        }

        public String getTheUploadUrlOfExtraInfo() {
            return theUploadUrlOfExtraInfo;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public void setTheUploadUrlOfExtraInfo(String theUploadUrlOfExtraInfo) {
            this.theUploadUrlOfExtraInfo = theUploadUrlOfExtraInfo;
        }
    }

    public static class ProjectProcessConstant {

        /**
         * ??????????????????id
         */
        public static final String PROCESS_ID  = "project";

        /**
         * ??????id??????
         */
        public static final String VAR_NAME_STUDENT = "student";

        /**
         * ??????????????????
         */
        public static final String VAR_NAME_SCHOOL = "school";

        /**
         * ????????????user task?????????
         */
        public static final String TASK_NAME_FIRST_LEVEL_REVIEW = "????????????";

        /**
         * ???????????? user task?????????
         */
        public static final String TASK_NAME_SECOND_LEVEL_REVIEW = "????????????";

        /**
         * ??????????????????user task?????????
         */
        public static final String TASK_NAME_UPLOAD_EXTRA_INFO = "??????????????????";


        /**
         * ???????????????
         * ??????????????????id
         */
        public static final String FORM_RECORD_ID = "recordId";

        /**
         * ???????????????
         * ????????????????????????
         */
        public static final String FORM_EXTRA_INFO_1 = "extra_info_1";

        /**
         * ???????????????
         * ?????????????????????
         */
        public static final String FORM_APPROVED_1 = "approved_1";
    }


    @ApiOperation(value = "????????????")
    @PostMapping(value = "/{projectId}/users{userId}")
    public boolean ParticipatingProject(@PathVariable Long projectId, @PathVariable Long userId) {
        //ignore argument verify

        //save the record to db

        Long savedRecordId = 3L;
        //start a new instance of the process
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(ProjectProcessConstant.VAR_NAME_SCHOOL, "??????????????????");
        variables.put(ProjectProcessConstant.VAR_NAME_STUDENT, String.valueOf(userId));
        variables.put(ProjectProcessConstant.FORM_RECORD_ID, savedRecordId);

        ProcessInstance instance = runtimeService.
                startProcessInstanceByKey(ProjectProcessConstant.PROCESS_ID, variables);
        if (instance == null) {
            return false;
        }else {
            return true;
        }
    }

    @ApiOperation(value = "???????????????????????????????????????")
    @GetMapping(value = "/project/approve/list")
    public @ResponseBody List<ProjectParticipateRequestRecord> getAllProjectParticipateRequest(String schoolName, Integer reviewLevel) {

        LOGGER.info("The school name is {}", schoolName);
        //get the taskList
        List<Task> tasks;
        if (reviewLevel.equals(1)) {
            tasks = taskService.createTaskQuery().
                    taskName(ProjectProcessConstant.TASK_NAME_FIRST_LEVEL_REVIEW).
                    taskCandidateGroup(schoolName).
                    list();
        }else {
            tasks = taskService.createTaskQuery().
                    taskName(ProjectProcessConstant.TASK_NAME_SECOND_LEVEL_REVIEW).
                    taskCandidateGroup(schoolName).
                    list();
        }

        List<ProjectParticipateRequestRecord> records = new ArrayList<ProjectParticipateRequestRecord>(tasks.size());
        tasks.forEach( task -> {
            ProjectParticipateRequestRecord record = new ProjectParticipateRequestRecord();
            String taskId = task.getId();
            Map<String, Object> variables = taskService.getVariables(taskId);

            Long studentId = Long.valueOf ( (String)variables.get(ProjectProcessConstant.VAR_NAME_STUDENT) );
            Long recordId = (Long) variables.get(ProjectProcessConstant.FORM_RECORD_ID);
            record.setStudentId(studentId);
            record.setProjectParticipateId(recordId);
            record.setTaskId(taskId);

            records.add(record);
        });

        return records;
    }

    @ApiOperation(value = "??????????????????")
    @PutMapping(value = "/project/participateRequests/{taskId}")
    public boolean approveProjectParticipateRequest(@PathVariable String taskId, boolean needExtraInfo, boolean passed, String schoolName) {
        Task task = taskService.createTaskQuery().
                taskCandidateGroup(schoolName).taskId(taskId).singleResult();
        if (task == null) {
            LOGGER.error("The task not found, task id is {}", taskId);
            return false;
        }else {
            //business logic here

            //Into next step
            LOGGER.info("The taskId is {}", taskId);
            Map<String, Object> variables = new HashMap<>();
            variables.put(ProjectProcessConstant.FORM_EXTRA_INFO_1,  needExtraInfo);
            variables.put(ProjectProcessConstant.FORM_APPROVED_1, passed);
            taskService.complete(task.getId(), variables);
            return true;
        }
    }

    @ApiOperation(value = "?????????????????????????????????????????????")
    @GetMapping(value = "/users/{userId}/extraInfo/list")
    public List<UploadExtraInfoRecord> getUploadExtraTask(Long userId) {
        List<Task> uploadExtraInfoTask =
                taskService.createTaskQuery().
                        taskAssignee(String.valueOf(userId)).
                        taskName(ProjectProcessConstant.TASK_NAME_UPLOAD_EXTRA_INFO).
                        list();

        List<UploadExtraInfoRecord> records = new ArrayList<>(uploadExtraInfoTask.size());
        uploadExtraInfoTask.forEach( task -> {
            UploadExtraInfoRecord record = new UploadExtraInfoRecord();
            record.setTaskId(task.getId());

            //the upload url of extra info is up to the variable
            record.setTheUploadUrlOfExtraInfo("www.google.com");

            records.add(record);
        });

        return records;
    }

    @ApiOperation(value = "???????????????????????????????????????")
    @PostMapping(value = "/{projectId}/users/{userId}/extraInfo")
    public boolean  uploadExtraInfo(@PathVariable Long projectId, @PathVariable Long userId,  String extraInfo, String taskId) {
        //must verify the task of the taskId pointing is belong the current user.
        Task task = taskService.createTaskQuery().
                taskAssignee(String.valueOf(userId)).
                taskName(ProjectProcessConstant.TASK_NAME_UPLOAD_EXTRA_INFO).
                taskId(taskId).
                singleResult();
        if (task == null) {
            LOGGER.error("The task not found.");
            LOGGER.error("the assignee is {}, taskName is {}, taskId is {}.", userId, ProjectProcessConstant.TASK_NAME_UPLOAD_EXTRA_INFO, taskId);
            return false;
        }else {
            //upload extra info to db.

            //business logic here

            //into next step
            taskService.complete(task.getId());
            return true;
        }
    }
}
