package org.geektimes.projects.user.service;

import org.geektimes.projects.user.context.ComponentContext;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.repository.DatabaseUserRepository;
import org.geektimes.projects.user.sql.DBConnectionManager;
import org.geektimes.projects.user.sql.LocalTransactional;
import org.geektimes.projects.user.validator.bean.validation.DelegatingValidator;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.AssertTrue;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

public class UserServiceTestingImpl {

    @Resource(name = "bean/Validator")
    private Validator validator;

    @Resource(name = "bean/DatabaseUserRepository")
    private DatabaseUserRepository databaseUserRepository;

    /**
     * transactions:  save->update->update(with error)
     * expected result: first update result
     * WORKING!!!!!
     */
    private User user;

    @LocalTransactional
    public void aopMethod1() throws SQLException {
        user = new User();
        user.setName("Zhikun Lao");
        user.setEmail("aaaaaaaaaaaaa@gmail.com");
        user.setPassword("aaaaaaaaaaaaa");
        user.setPhoneNumber("aaaaaaaaaaa");

        databaseUserRepository.save(user);

        ((UserServiceTestingImpl)ComponentContext.getInstance().getComponent("bean/UserServiceTesting")).aopMethod2();
    }

    @LocalTransactional
    public void aopMethod2() throws SQLException {
        user.setEmail("bbbbbbbbbbbbb@gmail.com");
        user.setPassword("bbbbbbbbbbbbb");
        user.setPhoneNumber("bbbbbbbbbbb");

        databaseUserRepository.update(user);

        ((UserServiceTestingImpl)ComponentContext.getInstance().getComponent("bean/UserServiceTesting")).aopMethod3();
    }

    @LocalTransactional
    public void aopMethod3() throws SQLException {
        user.setEmail("ccccccccccccc@gmail.com");
        user.setPassword("ccccccccccc");
        user.setPhoneNumber("ccccccccccc");

        databaseUserRepository.update(user);

        // trigger exception
        int b = 1/0;
    }
}
