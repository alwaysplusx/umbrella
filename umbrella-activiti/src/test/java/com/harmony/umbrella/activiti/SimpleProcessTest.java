package com.harmony.umbrella.activiti;

import static org.junit.Assert.*;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class SimpleProcessTest {

    @Test
    public void test() {
        // 加载配置文件产生流程配置
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti-context.xml");
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        int defaultSize = taskService.createTaskQuery().list().size();
        // 操作流程定义
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 部署流程模版
        repositoryService.createDeployment().addClasspathResource("org/moon/activiti/process/xml/leave.bpmn").deploy();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 根据留流程key<process id="leaveProcess"/>启动流程
        runtimeService.startProcessInstanceByKey("leaveProcess");
        List<Task> list = taskService.createTaskQuery().list();
        assertNotNull(list);
        assertEquals(defaultSize + 1, list.size());
    }

}
