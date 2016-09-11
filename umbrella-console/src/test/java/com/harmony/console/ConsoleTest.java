package com.harmony.console;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmony.console.log.controller.LogController;
import com.harmony.console.log.persistence.LogEntity;
import com.harmony.umbrella.log.Level.StandardLevel;

/**
 * @author wuxii@foxmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class ConsoleTest {

    @Autowired
    private LogController logController;

    @Test
    public void test() {
        assertNotNull(logController);
    }

    @Test
    public void testAppend() {
        //
        LogEntity logEntity = new LogEntity();
        logEntity.setAction("action");
        logEntity.setCreatedTime(Calendar.getInstance());
        logEntity.setCreatorCode("wuxi");
        logEntity.setCreatorId(1l);
        logEntity.setCreatorName("wuxii");
        logEntity.setId(2l);
        logEntity.setKey("key");
        logEntity.setLevel(StandardLevel.INFO);
        logEntity.setMessage("message");
        logEntity.setModule("module");
        logEntity.setOperatorHost("operatorHost");
        logEntity.setOperatorId("operatorId");
        logEntity.setOperatorName("operatorName");
        logEntity.setRequestTime(new Date());
        logEntity.setResponseTime(new Date());
        logEntity.setResult("result");
        logEntity.setStackLocation("stackLocation");
        logEntity.setThreadName("threadName");
        logEntity.setThrowableMessage("throwableMessage");
        logEntity.setContext("context");
        //
        logController.append(logEntity);
    }

}
