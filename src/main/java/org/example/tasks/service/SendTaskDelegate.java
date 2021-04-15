package org.example.tasks.service;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

/**
 *
 * @author 魏荣杰
 * @date 2021/4/14 15:17
 * @since
 * @version
 */
public class SendTaskDelegate implements JavaDelegate {


    @Override
    public void execute(DelegateExecution execution) throws Exception {

        System.out.println("send task running!!!!!");
    }
}
