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
public class ReceiveTaskEndDelegate implements ExecutionListener {

    @Resource
    private RuntimeService runtimeService;

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        Map<String, Object> variables = execution.getVariables();
        System.out.println("end flag==" + variables.get("flag").toString());
        System.out.println("end signList1==" + variables.get("signList1").toString());
        System.out.println("end signList2==" + variables.get("signList2").toString());

//        // 创建消息
//        MessageCorrelationBuilder messageCorrelation = runtimeService.createMessageCorrelation("receive");
//        //设置关联实例ID
//        messageCorrelation
//                .setVariables(variables)
//                .processInstanceId(execution.getProcessInstanceId());
//
//        //执行
//        messageCorrelation.correlate();
    }

}
