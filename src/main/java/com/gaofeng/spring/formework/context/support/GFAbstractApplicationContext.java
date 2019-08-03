package com.gaofeng.spring.formework.context.support;

/**
 * IOC容器实现的顶层设计
 * Created by Tom.
 */
public abstract class GFAbstractApplicationContext {
    //受保护，只提供给子类重写
    public void refresh() throws Exception {}
}
