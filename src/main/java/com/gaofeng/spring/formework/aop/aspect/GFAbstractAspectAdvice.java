package com.gaofeng.spring.formework.aop.aspect;

import java.lang.reflect.Method;

public class GFAbstractAspectAdvice {
    private Method aspectMethod;
    private Object aspectTarget;
    public GFAbstractAspectAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    public Object invokeAdviceMethod(GFJoinPoint joinPoint, Object returnValue, Throwable tx) throws Throwable{
        Class<?>[] paramType = this.aspectMethod.getParameterTypes();
        if(null == paramType || paramType.length == 0){
            return this.aspectMethod.invoke(this.aspectTarget);
        }else{
            Object[] args = new Object[paramType.length];
            for (int i=0;i<paramType.length;i++){
                if(paramType[i] == GFJoinPoint.class){
                    args[i] = joinPoint;
                }else if(paramType[i] == Object.class){
                    args[i] = returnValue;
                }else if(paramType[i] == Throwable.class){
                    args[i] = tx;
                }
            }
            return aspectMethod.invoke(this.aspectTarget,args);
        }
    }
}
