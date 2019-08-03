package com.gaofeng.spring.formework.beans.support;

import com.gaofeng.spring.formework.beans.config.GFBeanDefinition;
import com.gaofeng.spring.formework.context.support.GFAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GFDefaultListableBeanFactory extends GFAbstractApplicationContext {
    //用于存储bean的相关配置信息，伪ioc容器
    protected final Map<String, GFBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, GFBeanDefinition>();
}
