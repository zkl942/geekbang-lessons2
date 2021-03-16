package org.geektimes.projects.user.web.controller;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.service.UserServiceImpl;
import org.geektimes.projects.user.validator.bean.validation.DelegatingValidator;
import org.geektimes.web.mvc.controller.PageController;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.PrintWriter;
import java.util.Set;

@Path("/register")
public class SignupController implements PageController {

    @Resource(name = "bean/UserService")
    private UserServiceImpl userService;

    @Resource(name = "bean/DelegatingValidator")
    private DelegatingValidator delegatingValidator;

    @POST
    @Path("/validate") //  /register/validate
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String inputEmail = request.getParameter("inputEmail");
        String inputPassword = request.getParameter("inputPassword");
        String inputPhone = request.getParameter("inputPhone");
        String inputName = request.getParameter("inputName");

        User newUser = new User();
        newUser.setEmail(inputEmail);
        newUser.setPhoneNumber(inputPhone);
        newUser.setName(inputName);
        newUser.setPassword(inputPassword);

        Set<ConstraintViolation<User>> violations = delegatingValidator.validate(newUser);

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
