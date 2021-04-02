package org.geektimes.projects.user.web.controller;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.geektimes.jms.ActiveMQConsumer;
import org.geektimes.jms.ActiveMQProducer;
import org.geektimes.projects.mvc.controller.PageController;
import org.geektimes.projects.user.service.UserServiceTestingImpl;

import javax.annotation.Resource;
import javax.jms.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * demo playground
 */
@Path("/hello")
public class HelloWorldController implements PageController {

    @Resource(name = "bean/UserServiceTesting")
    private UserServiceTestingImpl userServiceTesting;

    @Resource(name = "jms/ActiveMQProducer")
    private ActiveMQProducer activeMQProducer;

    @Resource(name = "jms/ActiveMQConsumer")
    private ActiveMQConsumer activeMQConsumer;

    @GET
    @Path("/world") // /hello/world -> HelloWorldController
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        ServletContext servletContext = request.getServletContext();
        RequestDispatcher rd = servletContext.getRequestDispatcher("/");
        rd.forward(request, response);
    }

    @GET
    @Path("/aop")
    public void testAOP(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        userServiceTesting.aopMethod1();
    }

    @GET
    @Path("/activemq")
    public void testActiveMQJMS(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        activeMQProducer.runQueue();
        activeMQProducer.runTopic();
        activeMQConsumer.runQueue();
        activeMQConsumer.runTopic();
    }

}
