package org.example.tasks.service;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 *
 * @author 魏荣杰
 * @date 2021/4/14 15:27
 * @since
 * @version
 */
@Component
public class ReceiveTaskDelegate implements ExecutionListener {

    @Resource
    private RuntimeService runtimeService;

//    @Override
//    public void notify(DelegateTask delegateTask) {
//        Map<String, Object> variables = delegateTask.getVariables();
//        System.out.println("1--flag==" + variables.get("flag").toString());
//        System.out.println("1--signList1==" + variables.get("signList1").toString());
//        System.out.println("1--signList2==" + variables.get("signList2").toString());
//        //创建消息
//        MessageCorrelationBuilder messageCorrelation = runtimeService.createMessageCorrelation("receive");
//        //设置关联实例ID
//        messageCorrelation
//                .setVariables(variables)
//                .processInstanceId(delegateTask.getProcessInstanceId());
//
//        //执行
//        messageCorrelation.correlate();
//    }

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        Map<String, Object> variables = execution.getVariables();
        System.out.println("flag==" + variables.get("flag").toString());
        System.out.println("signList1==" + variables.get("signList1").toString());
        System.out.println("signList2==" + variables.get("signList2").toString());

    }

//    @Override
//    public void execute(DelegateExecution execution) throws Exception {
//
//        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
//        //创建消息
//        MessageCorrelationBuilder messageCorrelation = runtimeService.createMessageCorrelation("");
//        //设置关联实例ID
//        messageCorrelation.processInstanceId("instanceId");
//
//        //执行
//        messageCorrelation.correlate();
//    }
}
