package org.camunda.bpm.getstarted.loanapproval.camunda.tasks.service;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
/**
 *
 * @author 魏荣杰
 * @date 2021/4/8 19:39
 * @since
 * @version
 */
@Component
public class SmsServiceTask implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(SmsServiceTask.class);

    @Resource
    private TaskService taskService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Map<String, Object> variables = delegateExecution.getVariables();
        log.info("variables is {}", variables);

        String studentId = (String)variables.get("student");
        log.info("success send sms message to the student {}", studentId);
    }
}
