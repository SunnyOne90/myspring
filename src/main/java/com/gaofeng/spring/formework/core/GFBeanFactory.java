package com.gaofeng.spring.formework.core;

public interface GFBeanFactory {

    /**
     * 根据beanName从ioc容器中获取一个实例bean
     * @param beanName
     * @return
     * @throws Exception
     */
    Object getBean(String beanName) throws  Exception;

    Object getBean(Class<?> beanClass) throws Exception;

}
