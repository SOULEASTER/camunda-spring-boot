package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;

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


    @ApiOperation(value = "create")
    @PostMapping(value = "/create")
    public void createModel() {
        BpmnModelInstance bpmnModelInstance = Bpmn.createProcess()
                .id("process")
                .name("process")
                .executable()
                .startEvent()
                    .name("start")
                .parallelGateway("gateway1")

                    .userTask()
                        .id("user1")
                        .name("user1")
                    .userTask()
                        .id("user2")
                        .name("user2")
                .endEvent()
                    .name("end")
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

}
