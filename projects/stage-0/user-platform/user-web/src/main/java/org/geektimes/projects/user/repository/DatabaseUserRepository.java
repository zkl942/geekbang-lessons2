package org.geektimes.projects.user.repository;

import aop.transaction.TransactionalHandler;
import org.geektimes.projects.mvc.function.ThrowableFunction;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.sql.DBConnectionManager;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.lang.ClassUtils.primitiveToWrapper;
import static org.apache.commons.lang.ClassUtils.wrapperToPrimitive;

public class DatabaseUserRepository implements UserRepository {

    public static final String INSERT_USER_DML_SQL =
            "INSERT INTO users(name,password,email,phoneNumber) VALUES " +
                    "(?,?,?,?)";
    public static final String QUERY_ALL_USERS_DML_SQL = "SELECT id,name,password,email,phoneNumber FROM users";
    /**
     * 数据类型与 ResultSet 方法名映射
     */
    static Map<Class, String> resultSetMethodMappings = new HashMap<>();
    static Map<Class, String> preparedStatementMethodMappings = new HashMap<>();
    private static Logger logger = Logger.getLogger(DatabaseUserRepository.class.getName());
    /**
     * 通用处理方式
     */
    private static Consumer<Throwable> COMMON_EXCEPTION_HANDLER = e -> logger.log(Level.SEVERE, e.getMessage());

    static {
        resultSetMethodMappings.put(Long.class, "getLong");
        resultSetMethodMappings.put(String.class, "getString");

        preparedStatementMethodMappings.put(Long.class, "setLong"); // long
        preparedStatementMethodMappings.put(String.class, "setString"); //


    }

    @Resource(name = "bean/DBConnectionManager")
    private DBConnectionManager dbConnectionManager;
    @Resource(name = "bean/EntityManager")
    private EntityManager entityManager;

    public DatabaseUserRepository() {
    }

    private static String mapColumnLabel(String fieldName) {
        return fieldName;
    }

    private Connection getConnection() {
//        return dbConnectionManager.getConnection();
        return TransactionalHandler.connectionThreadLocal.get();
    }

    @Override
    public boolean save(User user) {
//        EntityTransaction transaction = entityManager.getTransaction();
//        transaction.begin();
//
//        try {
//            entityManager.persist(user);
//            transaction.commit();
//            return true;
//        } catch (Exception e) {
//            transaction.rollback();
//            return false;
//        }
//        return true;

        // not using hibernate here
//        String sql = "INSERT INTO users(name,password,email,phoneNumber) VALUES (?,?,?,?)";
//        int rowNum = executeUpdate(sql, user.getName(), user.getPassword(), user.getEmail(), user.getPhoneNumber());
//        return rowNum > 0;

        // use hibernate (hibernate works with jdbc transaction
        // (doesnt have to be EntityManager.getTransaction().begin()))
        entityManager.persist(user);
        return true;
    }

    /**
     * 通用的dml更新语句执行方法
     *
     * @param sql  sql语句
     * @param args sql语句参数
     * @return 更新语句影响的行数
     */
    protected int executeUpdate(String sql, Object... args) {
        Connection connection = getConnection();
        System.out.println("sql : conn: " + connection.hashCode());
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            prepareStatementSqlArgs(preparedStatement, args);
            int row = preparedStatement.executeUpdate();
            return row;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 准备 prepareStatement 的sql参数，反射调用 prepareStatment.setString(1,""); 方法
     *
     * @param preparedStatement 语句
     * @param args              参数
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void prepareStatementSqlArgs(PreparedStatement preparedStatement, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            Class<?> argType = arg.getClass();
            Class<?> wrapperType = primitiveToWrapper(argType);

            if (wrapperType == null) {
                wrapperType = argType;
            }

            String methodName = preparedStatementMethodMappings.get(argType);
            Method method = PreparedStatement.class.getMethod(methodName, int.class, wrapperType);
            method.invoke(preparedStatement, i + 1, args[i]);
        }
    }

    @Override
    public boolean deleteById(Long userId) {
        return false;
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE USERS SET EMAIL = ?, PASSWORD = ?, PHONENUMBER = ?";
        int rowNum = executeUpdate(sql, user.getEmail(), user.getPassword(), user.getPhoneNumber());
        return rowNum > 0;
    }

    @Override
    public User getById(Long userId) {
        return null;
    }

    @Override
    public User getByNameAndPassword(String userName, String password) {
        return executeQuery("SELECT id,name,password,email,phoneNumber FROM users WHERE name=? and password=?",
                resultSet -> {
                    // TODO
                    return new User();
                }, COMMON_EXCEPTION_HANDLER, userName, password);
    }

    @Override
    public Collection<User> getAll() {
        return executeQuery("SELECT id,name,password,email,phoneNumber FROM users", resultSet -> {
            // BeanInfo -> IntrospectionException
            BeanInfo userBeanInfo = Introspector.getBeanInfo(User.class, Object.class);
            List<User> users = new ArrayList<>();
            while (resultSet.next()) { // 如果存在并且游标滚动 // SQLException
                User user = new User();
                for (PropertyDescriptor propertyDescriptor : userBeanInfo.getPropertyDescriptors()) {
                    String fieldName = propertyDescriptor.getName();
                    Class fieldType = propertyDescriptor.getPropertyType();
                    String methodName = resultSetMethodMappings.get(fieldType);
                    // 可能存在映射关系（不过此处是相等的）
                    String columnLabel = mapColumnLabel(fieldName);
                    Method resultSetMethod = ResultSet.class.getMethod(methodName, String.class);
                    // 通过放射调用 getXXX(String) 方法
                    Object resultValue = resultSetMethod.invoke(resultSet, columnLabel);
                    // 获取 User 类 Setter方法
                    // PropertyDescriptor ReadMethod 等于 Getter 方法
                    // PropertyDescriptor WriteMethod 等于 Setter 方法
                    Method setterMethodFromUser = propertyDescriptor.getWriteMethod();
                    // 以 id 为例，  user.setId(resultSet.getLong("id"));
                    setterMethodFromUser.invoke(user, resultValue);
                }
            }
            return users;
        }, e -> {
            // 异常处理
        });
    }

    /**
     * @param sql
     * @param function
     * @param <T>
     * @return
     */
    protected <T> T executeQuery(String sql, ThrowableFunction<ResultSet, T> function,
                                 Consumer<Throwable> exceptionHandler, Object... args) {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                Class argType = arg.getClass();

                Class wrapperType = wrapperToPrimitive(argType);

                if (wrapperType == null) {
                    wrapperType = argType;
                }

                // Boolean -> boolean
                String methodName = preparedStatementMethodMappings.get(argType);
                Method method = PreparedStatement.class.getMethod(methodName, wrapperType);
                method.invoke(preparedStatement, i + 1, args);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            // 返回一个 POJO List -> ResultSet -> POJO List
            // ResultSet -> T
            return function.apply(resultSet);
        } catch (Throwable e) {
            exceptionHandler.accept(e);
        }
        return null;
    }
}
