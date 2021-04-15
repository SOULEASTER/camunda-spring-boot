package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.example.tasks.service.ReceiveTaskDelegate;
import org.example.tasks.service.ReceiveTaskEndDelegate;
import org.example.tasks.service.SendTaskDelegate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author 魏荣杰
 * @date 2021/4/12 19:14
 * @since
 * @version
 */
@Api
@RestController
@RequestMapping("/process")
public class ProcessController {

    @Resource
    private RuntimeService runtimeService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private TaskService taskService;


    @ApiOperation(value = "create")
    @PostMapping(value = "/create")
    public void createModel() {
        BpmnModelInstance bpmnModelInstance = Bpmn.createProcess()
                .id("process").name("process")
                .executable()
                .startEvent()
                    .name("start")
                .receiveTask()
                    .id("receiveTask1").name("receiveTask1").message("receive").camundaExecutionListenerClass("start", ReceiveTaskDelegate.class).camundaExecutionListenerClass("end", ReceiveTaskEndDelegate.class)
                .exclusiveGateway("gateway1")
                    .condition("会签", "#{flag == 1}")
                    .userTask()
                        .id("user1").name("user1").camundaAssignee("${sign1}")
                        .multiInstance().parallel().completionCondition("${nrOfActiveInstances == nrOfInstances}").camundaCollection("signList1").camundaElementVariable("sign1").multiInstanceDone()
                    .sendTask()
                        .id("send1").camundaClass(SendTaskDelegate.class)
                    .endEvent()
                .moveToLastGateway()
                    .condition("或签", "#{flag == 2}")
                    .userTask()
                        .id("user2").name("user2").camundaAssignee("${sign2}")
                        .multiInstance().parallel().camundaCollection("${nrOfCompletedInstances>0}").camundaCollection("signList2").camundaElementVariable("sign2").multiInstanceDone()
                    .endEvent()
                .done();

        Bpmn.validateModel(bpmnModelInstance);

        String name = "process" + System.currentTimeMillis() + ".bpmn";
        File file = new File("/Users/w.rajer/Development/IdeaProjects/PersonalDemo/camunda-spring-boot/src/main/resources/model/" + name);
        Bpmn.writeModelToFile(file, bpmnModelInstance);

        // 部署
        repositoryService.createDeployment()
                .name(name)
                .addModelInstance(name, bpmnModelInstance)
                .deploy();
    }

    @ApiOperation(value = "start")
    @PostMapping(value = "/start")
    public void start(@RequestParam Integer flag,@RequestParam List<String> signList1, @RequestParam List<String> signList2) {

        HashMap<String, Object> variables = new HashMap<>();
        variables.put("flag", flag);
        variables.put("signList1", signList1);
        variables.put("signList2", signList2);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process", variables);
        if (processInstance == null) {
            System.out.println("1--process start failed");
        } else {
            System.out.println("1--process start id" + processInstance.getId());

            //创建消息
            MessageCorrelationBuilder messageCorrelation = runtimeService.createMessageCorrelation("receive");
            //设置关联实例ID
            messageCorrelation
                    .setVariables(variables)
                    .processInstanceId(processInstance.getId());

            //执行
            messageCorrelation.correlate();
        }


//        HashMap<String, Object> variables = new HashMap<>();
//        variables.put("flag", flag);
//        variables.put("signList1", signList1);
//        variables.put("signList2", signList2);
//
//        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process", variables);
//        if (processInstance == null) {
//            return false;
//        } else {
//            System.out.println("process start id" + processInstance.getId());
//            return true;
//        }
    }

    @ApiOperation(value = "status")
    @PostMapping(value = "/status")
    public void status() {

    }
}
