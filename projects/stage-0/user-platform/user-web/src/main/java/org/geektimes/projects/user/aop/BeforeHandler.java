package org.geektimes.projects.user.aop;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public interface BeforeHandler extends Handler {

    Object before(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable;
}
