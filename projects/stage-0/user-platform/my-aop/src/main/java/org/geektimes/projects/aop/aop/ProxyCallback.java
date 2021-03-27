package org.geektimes.projects.aop.aop;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class ProxyCallback implements MethodInterceptor {
    private Object originalComponent;
    private ArrayList<BeforeHandler> beforeHandlers = new ArrayList<>();
    private ArrayList<AfterHandler> afterHandlers = new ArrayList<>();
    private ArrayList<ErrorHandler> errorHandlers = new ArrayList<>();

    public ProxyCallback(Object originalComponent) {
        this.originalComponent = originalComponent;
    }

    public void addBeforeHandler(BeforeHandler beforeHandler) {
        beforeHandlers.add(beforeHandler);
    }

    public void addAfterHandler(AfterHandler afterHandler) {
        afterHandlers.add(afterHandler);
    }

    public void addErrorHandler(ErrorHandler errorHandler) {
        errorHandlers.add(errorHandler);
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (beforeHandlers != null && beforeHandlers.size() != 0) {
            for (BeforeHandler beforeHandler : beforeHandlers) {
                // invoke before(...) one by one
                beforeHandler.before(obj, method, args, proxy);
            }
        }
        // execute original method
        Object result = null;
        try {
            result = method.invoke(originalComponent, args);
        } catch (Throwable throwable) {
            // have Exeception, invoke errorHandlers
            if (errorHandlers != null && errorHandlers.size() != 0) {
                for (ErrorHandler errorHandler : errorHandlers) {
                    // invoke error(...) one by one
                    errorHandler.error(obj, method, args, proxy);
                }
            }
            return result;
        }

        // no exception, invoke afterHandlers
        if (afterHandlers != null && afterHandlers.size() != 0) {
            for (AfterHandler afterHandler : afterHandlers) {
                // invoke error(...) one by one
                afterHandler.after(obj, method, args, proxy, result);
            }
        }
        return result;
    }

}
