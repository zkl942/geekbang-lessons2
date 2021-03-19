package org.geektimes.projects.user.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;

public class EnhancerClassDemo {

    public static void main(String[] args) throws Exception {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(SampleClass.class);
        //使用FixedValue，拦截返回值，每次返回固定值"Doge say hello!"
        enhancer.setCallback((FixedValue) () -> "Doge say hello!");
        SampleClass sampleClass = (SampleClass) enhancer.create();
        System.out.println(sampleClass.sayHello("throwable-10086"));
        System.out.println(sampleClass.sayHello("throwable-doge"));
        System.out.println(sampleClass.toString());
        System.out.println(sampleClass.getClass());
        System.out.println(sampleClass.hashCode());
    }
}