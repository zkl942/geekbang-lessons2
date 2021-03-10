package org.geektimes.projects.user.web.controller;

import org.geektimes.context.ComponentContext;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.projects.user.service.UserServiceImpl;
import org.geektimes.web.mvc.controller.PageController;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Set;

@Path("/register")
public class SignupController implements PageController {

//    not part of JNDI
//    @Resource(name = "bean/UserService")
    private UserServiceImpl userService;

    @GET
    @POST
    @Path("/validate") //  /register/validate
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        userService = ComponentContext.getInstance().getComponent("bean/UserService");
//        Enumeration paramNames = request.getParameterNames();
//        while(paramNames.hasMoreElements()) {
//            String paramName = (String) paramNames.nextElement();
//        }
//        Long inputId = Long.parseLong(request.getParameter("inputId"));
        String inputEmail = request.getParameter("inputEmail");
        String inputPassword = request.getParameter("inputPassword");
        String inputPhone = request.getParameter("inputPhone");
        String inputName = request.getParameter("inputName");

        User newUser = new User();
//        newUser.setId(inputId);
        newUser.setEmail(inputEmail);
        newUser.setPhoneNumber(inputPhone);
        newUser.setName(inputName);
        newUser.setPassword(inputPassword);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        // 手动校验
        Set<ConstraintViolation<User>> violations = validator.validate(newUser);

        violations.forEach(c -> {
            System.out.println(c.getMessage());
        });

        ServletContext servletContext = request.getServletContext();

        if (userService.register(newUser)) {
            // success
            RequestDispatcher rd = servletContext.getRequestDispatcher("/success.jsp");
            rd.forward(request, response);
        } else {
            // failure
            // include / with failure message
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.print("<center><h1 class=\"h3 mb-3 font-weight-normal\">Incorrect registration info!</h1></center>");
            RequestDispatcher rd = servletContext.getRequestDispatcher("/");
            rd.include(request, response);
        }
        return "";
    }
}
