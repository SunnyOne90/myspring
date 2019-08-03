package com.gaofeng.spring.formework.aop.support;

public class GFAdvisedSupport {
    //实例化类的class对象
    private Class<?> targetClass;
    //实例化的类
    private Object target;

    public boolean pointCutMatch(){
        return false;
    }
}
