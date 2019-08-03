package com.gaofeng.spring.formework.beans.config;

public class GFBeanDefinition {
    private String beanClassName; //bean的名称
    private boolean lazyInit = false;//是否需要延迟加载的bean信息
    private String factoryBeanName;//在工厂中bean的名称

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}

