package com.gaofeng.spring.formework.context;

import com.gaofeng.spring.formework.annotation.GFAutowired;
import com.gaofeng.spring.formework.annotation.GFController;
import com.gaofeng.spring.formework.annotation.GFService;
import com.gaofeng.spring.formework.aop.support.GFAdvisedSupport;
import com.gaofeng.spring.formework.beans.GFBeanWrapper;
import com.gaofeng.spring.formework.beans.config.GFBeanDefinition;
import com.gaofeng.spring.formework.beans.config.GFBeanPostProcessor;
import com.gaofeng.spring.formework.beans.support.GFBeanDefinitionReader;
import com.gaofeng.spring.formework.beans.support.GFDefaultListableBeanFactory;
import com.gaofeng.spring.formework.core.GFBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class GFApplicationContext extends GFDefaultListableBeanFactory implements GFBeanFactory {
    private String[] configLoactions;

    private GFBeanDefinitionReader reader;
    //单例的IOC容器缓存
    private Map<String,Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>();
    //通用的IOC容器
    private Map<String,GFBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, GFBeanWrapper>();

    public GFApplicationContext(String... configLoactions) {
        this.configLoactions = configLoactions;
        //在applicationContext中调用refresh方法，初始化IOC容器
        try {
            refresh();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        //1.定位配置文件
        reader = new GFBeanDefinitionReader(this.configLoactions);
        //2.加载配置文件，扫描相关的类,把他们封装成beanDefinition
        List<GFBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //3.注册，把配置信息放到容器中
        doRegisterBeanDefinition(beanDefinitions);
        //4.把不是延迟加载的类，提前初始化
        doAutowrited();
    }

    private void doAutowrited() {
        for (Map.Entry<String, GFBeanDefinition> enty : super.beanDefinitionMap.entrySet()) {
            String beanName = enty.getKey();
            if(!enty.getValue().isLazyInit()){
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将beanDefinition注册到伪IOC容器中
     * @param beanDefinitions
     * @throws Exception
     */
    private void doRegisterBeanDefinition(List<GFBeanDefinition> beanDefinitions) throws Exception{
        for(GFBeanDefinition beanDefinition:beanDefinitions){
            if(beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw new Exception("The “" + beanDefinition.getFactoryBeanName() + "” is exists!!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        GFBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        Object instance = null;
        //这个逻辑还不严谨，自己可以去参考Spring源码
        //工厂模式 + 策略模式
        GFBeanPostProcessor postProcessor = new GFBeanPostProcessor();
        postProcessor.postProcessBeforeInitialization(instance,beanName);
        //实例化bean
        instance = instantiateBean(beanName,beanDefinition);
        //将实例化后的bean封装到beanWrapper中
        GFBeanWrapper beanWrapper = new GFBeanWrapper(instance);
        //将beanWrapper保存到ioc容器中
        this.factoryBeanInstanceCache.put(beanName,beanWrapper);

        postProcessor.postProcessAfterInitialization(instance,beanName);
        //依赖注入
        populateBean(beanWrapper);

        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();


    }

    private void populateBean(GFBeanWrapper beanWrapper) {
        //获取实例对象
        Object instance = beanWrapper.getWrappedInstance();
        // 获取实例对象中得class对象
        Class<?> clazz = beanWrapper.getWrappedClass();
        //判断只有加了注解的类才可以进行依赖注入
        if(!(clazz.isAnnotationPresent(GFController.class) || clazz.isAnnotationPresent(GFService.class)))return;
        //获取class类中得所有信息
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //判断对象中是否存在Autowired注解
            if(!field.isAnnotationPresent(GFAutowired.class))continue;
            //如果存在获取注解上的值
            GFAutowired autowired =  field.getAnnotation(GFAutowired.class);
            String autowiredBeanName =  autowired.value().trim();
            if("".equals(autowiredBeanName)){
                //如果值不存在那么通过类型进行获取
                autowiredBeanName = field.getType().getName();
            }
            //添加强制访问
            field.setAccessible(true);
            if(this.factoryBeanInstanceCache.get(autowiredBeanName) == null){ continue; }
            try {
                //通过反射进行赋值
                field.set(instance,this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 实例化bean
     * @param beanName
     * @param beanDefinition
     * @return
     */
    private Object instantiateBean(String beanName, GFBeanDefinition beanDefinition) {
        //1、拿到要实例化的对象的类名
        String className = beanDefinition.getBeanClassName();
        //2、反射实例化，得到一个对象
        Object instance = null;
        try {
            if(this.factoryBeanObjectCache.containsKey(className)){
                instance = factoryBeanObjectCache.get(className);
            }else{
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                // TODO: 2019/8/3 加入AOP逻辑
                GFAdvisedSupport advisedSupport = new GFAdvisedSupport();

                this.factoryBeanObjectCache.put(className,instance);
                this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(),instance);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return instance;

    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    /**
     * 加载切面的相关配置信息
     * @param gpBeanDefinition
     * @return
     */
    private GFAdvisedSupport instantionAopConfig(GFBeanDefinition gpBeanDefinition){
        return null;
    }

    //将伪IOC容器中得配置信息的key取出来
    public String[] getBeanDefinitionNames(){
       return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public Properties getconfig(){
        return reader.getConfig();
    }
}
