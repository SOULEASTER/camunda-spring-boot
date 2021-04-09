package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BpmnModelElementInstance;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.xml.ModelInstance;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author 魏荣杰
 * @date 2021/4/8 19:22
 * @since
 * @version
 */
@Api
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private RuntimeService runtimeService;
    @Resource
    private RepositoryService repositoryService;

    @ApiOperation(value = "SAdfds")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public void list() {

    }

    @ApiOperation(value = "createModel")
    @RequestMapping(value = "/createModel", method = RequestMethod.POST)
    public void createModel() throws IOException {
        // create an empty model
        BpmnModelInstance modelInstance = Bpmn.createEmptyModel();
        Definitions definitions = modelInstance.newInstance(Definitions.class);
        definitions.setTargetNamespace("http://camunda.org/examples");
        modelInstance.setDefinitions(definitions);

        // create the process
        Process process = createElement(definitions, "process-with-one-task", Process.class);

        // create start event, user task and end event
        StartEvent startEvent = createElement(process, "start", StartEvent.class);
        UserTask task1 = createElement(process, "task1", UserTask.class);
        task1.setName("User Task");
        EndEvent endEvent = createElement(process, "end", EndEvent.class);

        // create the connections between the elements
        createSequenceFlow(process, startEvent, task1);
        createSequenceFlow(process, task1, endEvent);

        // validate and write model to file
        Bpmn.validateModel(modelInstance);
        File folders = new File("/Users/w.rajer/Development/IdeaProjects/PersonalDemo/camunda-spring-boot/src/main/resources");
        File file = File.createTempFile("new", ".bpmn", folders);
        Bpmn.writeModelToFile(file, modelInstance);
//        file.delete();
//        file.deleteOnExit();

        repositoryService.createDeployment().addModelInstance("process-with-one-task", modelInstance).deploy();

    }

    @ApiOperation(value = "createModelByFluent")
    @RequestMapping(value = "/createModelByFluent", method = RequestMethod.POST)
    public void createModelByFluent() {

    }


    @ApiOperation(value = "start")
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public boolean start() {
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("/Users/w.rajer/Development/IdeaProjects/PersonalDemo/camunda-spring-boot/src/main/resources/new923686982284008251.bpmn"));
        ModelElementInstance modelElementInstance = bpmnModelInstance.getModelElementById("process-with-one-task");
        ModelInstance modelInstance = modelElementInstance.getModelInstance();
        runtimeService.activateProcessInstanceById("process-with-one-task");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process-with-one-task");
        if (processInstance == null) {
            return false;
        } else {
            return true;
        }
    }

    public SequenceFlow createSequenceFlow(Process process, FlowNode from, FlowNode to) {
        String identifier = from.getId() + "-" + to.getId();
        SequenceFlow sequenceFlow = createElement(process, identifier, SequenceFlow.class);
        process.addChildElement(sequenceFlow);
        sequenceFlow.setSource(from);
        from.getOutgoing().add(sequenceFlow);
        sequenceFlow.setTarget(to);
        to.getIncoming().add(sequenceFlow);
        return sequenceFlow;
    }

    protected <T extends BpmnModelElementInstance> T createElement(BpmnModelElementInstance parentElement, String id, Class<T> elementClass) {
        T element = parentElement.getModelInstance().newInstance(elementClass);
        element.setAttributeValue("id", id, true);
        parentElement.addChildElement(element);
        return element;
    }

}
