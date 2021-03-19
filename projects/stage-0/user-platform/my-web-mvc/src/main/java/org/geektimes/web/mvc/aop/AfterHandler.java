package org.geektimes.web.mvc.aop;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public interface AfterHandler extends Handler {

    Object after(Object obj, Method method, Object[] args, MethodProxy proxy, Object result) throws Throwable;
}
