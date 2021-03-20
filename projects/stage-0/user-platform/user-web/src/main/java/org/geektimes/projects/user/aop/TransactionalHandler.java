package org.geektimes.projects.user.aop;

import net.sf.cglib.proxy.MethodProxy;
import org.geektimes.projects.user.context.ComponentContext;

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
     */
    public static ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            Connection conn = null;
            try {
                conn = ((DataSource) ComponentContext.getInstance().getComponent("jdbc/UserPlatformDB")).getConnection();
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
     * we are only implementing 2-level nested transaction for simplicity
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
