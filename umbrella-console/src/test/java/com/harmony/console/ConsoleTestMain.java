package com.harmony.console;

import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Test;

import com.harmony.console.log.persistence.LogEntity;
import com.harmony.umbrella.log.Level.StandardLevel;

/**
 * @author wuxii@foxmail.com
 */
public class ConsoleTestMain {

    @Test
    public void testLogAppend() {
        // log entity
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

        Response response = ClientBuilder.newClient().target("http://localhost:8080/log/append").request().post(Entity.entity(logEntity, MediaType.APPLICATION_JSON));
        String responseText = response.readEntity(String.class);
        System.out.println(responseText);
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        server.setHandler(new WebAppContext("src/main/webapp", "/"));
        server.start();
    }

}
