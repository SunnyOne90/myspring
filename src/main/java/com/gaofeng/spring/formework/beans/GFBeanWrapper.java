package com.gaofeng.spring.formework.beans;

public class GFBeanWrapper {

    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public GFBeanWrapper(Object wrappedInstance){
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance(){
        return this.wrappedInstance;
    }

    // 返回代理以后的Class
    // 可能会是这个 $Proxy0
    public Class<?> getWrappedClass(){
        return this.wrappedInstance.getClass();
    }
}
