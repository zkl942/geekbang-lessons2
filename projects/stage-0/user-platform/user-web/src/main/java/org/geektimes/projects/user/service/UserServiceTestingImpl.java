package org.geektimes.projects.user.service;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.repository.DatabaseUserRepository;
import org.geektimes.projects.user.sql.LocalTransactional;

import javax.annotation.Resource;
import javax.validation.Validator;
import java.sql.SQLException;

public class UserServiceTestingImpl {

    @Resource(name = "bean/Validator")
    private Validator validator;

    @Resource(name = "bean/DatabaseUserRepository")
    private DatabaseUserRepository databaseUserRepository;

    /**
     * 通过 @Resource 注入一个自身的代理对象
     * 解决cglib无法拦截类内部调用的问题
     * https://chenjianhui.site/2020-07-15-spring-aop-internal-call/
     */
    @Resource(name = "bean/UserServiceTesting")
    private UserServiceTestingImpl userServiceTestingImpl;

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

        /**
         * cglib内部调用不触发拦截器
         */
//        aopMethod2();
//        ((UserServiceTestingImpl)ComponentContext.getInstance().getComponent("bean/UserServiceTesting")).aopMethod2();
        userServiceTestingImpl.aopMethod2();
    }

    @LocalTransactional
    public void aopMethod2() throws SQLException {
        user.setEmail("bbbbbbbbbbbbb@gmail.com");
        user.setPassword("bbbbbbbbbbbbb");
        user.setPhoneNumber("bbbbbbbbbbb");

        databaseUserRepository.update(user);

//        ((UserServiceTestingImpl)ComponentContext.getInstance().getComponent("bean/UserServiceTesting")).aopMethod3();
        userServiceTestingImpl.aopMethod3();
    }

    @LocalTransactional
    public void aopMethod3() throws SQLException {
        user.setEmail("ccccccccccccc@gmail.com");
        user.setPassword("ccccccccccc");
        user.setPhoneNumber("ccccccccccc");

        databaseUserRepository.update(user);

        // trigger exception
        int b = 1 / 0;
    }
}
