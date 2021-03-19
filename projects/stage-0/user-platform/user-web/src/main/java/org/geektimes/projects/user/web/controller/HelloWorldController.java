package org.geektimes.projects.user.web.controller;

import org.geektimes.projects.user.service.UserServiceImpl;
import org.geektimes.projects.user.service.UserServiceTestingImpl;
import org.geektimes.web.mvc.controller.PageController;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * 输出 “Hello,World” Controller
 */
@Path("/hello")
public class HelloWorldController implements PageController {

    @Resource(name = "bean/UserServiceTesting")
    private UserServiceTestingImpl userServiceTesting;

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
        userServiceTesting.aopMethod();
    }
}
