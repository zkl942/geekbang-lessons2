package org.geektimes.projects.aop.cglib;

import javax.annotation.Resource;

public class SampleClass {

    @Resource
    private int hello;

    public String sayHello(String name) {
        return String.format("%s say hello!", name);
    }
}
