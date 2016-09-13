package com.harmony.console.log.controller;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Controller;

import com.harmony.console.log.persistence.LogEntity;
import com.harmony.console.log.service.LogService;

/**
 * @author wuxii@foxmail.com
 */
@Controller
@Path("/log")
public class LogController {

    @Inject
    private LogService logService;

    @POST
    @Path("/append")
    @Produces(MediaType.APPLICATION_JSON)
    public String append(LogEntity logEntity) {
        logService.persist(logEntity);
        return "{\"success\":true}";
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LogEntity> all() {
        return logService.findAll();
    }

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return "Log Service Api";
    }

}
