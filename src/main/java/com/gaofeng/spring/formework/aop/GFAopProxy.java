package com.gaofeng.spring.formework.aop;

/**
 * Created by Tom.
 */
public interface GFAopProxy {


    Object getProxy();


    Object getProxy(ClassLoader classLoader);
}
