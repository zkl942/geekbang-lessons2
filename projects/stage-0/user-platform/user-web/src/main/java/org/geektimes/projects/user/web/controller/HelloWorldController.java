package org.geektimes.projects.user.web.controller;

import org.geektimes.jms.ActiveMQConsumer;
import org.geektimes.jms.ActiveMQProducer;
import org.geektimes.projects.mvc.controller.PageController;
import org.geektimes.projects.user.service.UserServiceTestingImpl;

import javax.annotation.Resource;
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
        activeMQConsumer.receiveFromQueue();
        activeMQConsumer.receiveFromTopic();

        activeMQProducer.sendToQueue(5);
        activeMQProducer.sendToTopic(5);

        activeMQProducer.sendToTopic(3);
        activeMQProducer.sendToQueue(2);

//        activeMQProducer.close();
//        activeMQConsumer.close();
    }

}
