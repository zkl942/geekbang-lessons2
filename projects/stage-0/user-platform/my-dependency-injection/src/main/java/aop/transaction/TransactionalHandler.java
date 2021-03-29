package aop.transaction;

import aop.generic.AfterHandler;
import aop.generic.BeforeHandler;
import aop.generic.ErrorHandler;
import net.sf.cglib.proxy.MethodProxy;
import org.geektimes.projects.di.context.ClassicComponentContext;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.HashMap;

public class TransactionalHandler implements BeforeHandler, AfterHandler, ErrorHandler {

    /**
     * Hibernate does not explicitly support nested transactions,
     * using a JDBC 3.0 driver that is able to create savepoints can achieve this.
     * <p>
     * ThreadLocal:
     * This class provides thread-local variables. These variables differ from their normal
     * counterparts in that each thread that accesses one (via its get or set method) has its own,
     * independently initialized copy of the variable. ThreadLocal instances are typically private
     * static fields in classes that wish to associate state with a thread (e.g., a user ID or
     * Transaction ID).
     * Each thread holds an implicit reference to its copy of a thread-local
     * variable as long as the thread is alive and the ThreadLocal instance is accessible;
     * after a thread goes away, all of its copies of thread-local instances are subject to
     * garbage collection (unless other references to these copies exist).
     * <p>
     * AFAIK, the typical use of ThreadLocal<Connection> is to store a unique database connection
     * per thread, so that the same connection can be used in different methods in your business
     * logic without the need of passing it as a parameter each time. Because the common servlet
     * container implementation uses a thread to fulfill an HTTP request, then two different requests
     * are guaranteed to use two different database connections.
     */
    public static ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal() {
        /**
         * initialValue method will be invoked the first time a thread
         * accesses the variable with the get method
         */
        @Override
        protected Object initialValue() {
            Connection conn = null;
            try {
                /**
                 * DataSource 本身就是一个pool（JNDI），这里相当于从pool里面取出一个connection来作为Thread的专用
                 * maxActive="100" maxIdle="30" maxWait="10000"
                 */
                conn = ((DataSource) ClassicComponentContext.getInstance().getComponent("jdbc/UserPlatformDB")).getConnection();
                /**
                 * autoCommit mode must be off, otherwise each time you call
                 * flush you'll be commiting straight to the DB.
                 */
                conn.setAutoCommit(false);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return conn;
        }
    };

    /**
     * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/TransactionDefinition.html
     * we are only implementing PROPAGATION_REQUIRED for simplicity
     * register 内部调用 update
     */
    public static ThreadLocal<HashMap<String, Savepoint>> savepointThreadLocal = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            return new HashMap<String, Savepoint>();
        }
    };

    /**
     * keep track of nested level.
     * Every nested invocation adds
     * Every return reduces 1
     * When level is 0 in after, connection is reday to commit;
     */
    public static ThreadLocal<Integer> nestedLevelThreadLocal = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            return 0;
        }
    };

    /**
     * move through each nested transaction.
     * For each nested transaction, create another different savePoint
     *
     * @param obj
     * @param method
     * @param args
     * @param proxy
     * @return
     * @throws Throwable
     */
    @Override
    public Object before(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Savepoint savepoint = connectionThreadLocal.get().setSavepoint(method.getName());
        savepointThreadLocal.get().put(method.getName(), savepoint);
        nestedLevelThreadLocal.set(nestedLevelThreadLocal.get() + 1);
        return null;
    }

    @Override
    public Object after(Object obj, Method method, Object[] args, MethodProxy proxy, Object result) throws Throwable {
        nestedLevelThreadLocal.set(nestedLevelThreadLocal.get() - 1);
        if (nestedLevelThreadLocal.get() == 0) {
            connectionThreadLocal.get().commit();
        }
        return null;
    }

    @Override
    public Object error(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        nestedLevelThreadLocal.set(nestedLevelThreadLocal.get() - 1);
        Savepoint savepoint = savepointThreadLocal.get().get(method.getName());
        connectionThreadLocal.get().rollback(savepoint);
        return null;
    }
}
