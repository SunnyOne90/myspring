package com.gaofeng.spring.formework.aop.intercept;

import com.gaofeng.spring.formework.aop.aspect.GFJoinPoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GFMethodInvocation implements GFJoinPoint {
    private Object proxy;//代理对象
    private Method method;//目标对象方法
    private Object target;//目标对象
    private Object [] arguments;//目标对象的参数
    private List<Object> interceptorsAndDynamicMethodMatchers;
    private Class<?> targetClass;
    //定义一个索引，从-1开始来记录当前拦截器执行的位置
    private int currentInterceptorIndex = -1;
    private Map<String, Object> userAttributes;
    public GFMethodInvocation(Object proxy, Object target, Method method, Object[] args, Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
        this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
        this.method = method;
        this.arguments = args;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }
    public Object proceed() throws Throwable{
        //如果Interceptor执行完了，则执行joinPoint
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
            return this.method.invoke(this.target,this.arguments);
        }

        Object interceptorOrInterceptionAdvice =
                this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
        //如果要动态匹配joinPoint
        if (interceptorOrInterceptionAdvice instanceof GFMethodInterceptor) {
            GFMethodInterceptor mi =
                    (GFMethodInterceptor) interceptorOrInterceptionAdvice;
            return mi.invoke(this);
        } else {
            //动态匹配失败时,略过当前Intercetpor,调用下一个Interceptor
            return proceed();
        }
    }
    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        if (value != null) {
            if (this.userAttributes == null) {
                this.userAttributes = new HashMap<String,Object>();
            }
            this.userAttributes.put(key, value);
        }
        else {
            if (this.userAttributes != null) {
                this.userAttributes.remove(key);
            }
        }
    }

    @Override
    public Object getUserAttribute(String key) {
        return (this.userAttributes != null ? this.userAttributes.get(key) : null);
    }
}
