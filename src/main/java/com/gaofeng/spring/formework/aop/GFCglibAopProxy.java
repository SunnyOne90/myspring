package com.gaofeng.spring.formework.aop;


import com.gaofeng.spring.formework.aop.support.GFAdvisedSupport;

/**
 * Created by Tom on 2019/4/14.
 */
public class GFCglibAopProxy implements  GFAopProxy {
    public GFCglibAopProxy(GFAdvisedSupport config) {
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
