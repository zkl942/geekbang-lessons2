package aop.generic;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public interface ErrorHandler extends Handler {

    Object error(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable;

}
