package org.geektimes.web.mvc.aop;

import net.sf.cglib.proxy.MethodProxy;
import org.geektimes.web.mvc.aop.AfterHandler;
import org.geektimes.web.mvc.aop.BeforeHandler;
import org.geektimes.web.mvc.aop.ErrorHandler;

import java.lang.reflect.Method;

public class TransactionalHandler implements BeforeHandler, AfterHandler, ErrorHandler {
    @Override
    public Object after(Object obj, Method method, Object[] args, MethodProxy proxy, Object result) throws Throwable {
        System.out.println("after");
        return null;
    }

    @Override
    public Object before(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("before");
        return null;
    }

    @Override
    public Object error(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("error");
        return null;
    }
}
