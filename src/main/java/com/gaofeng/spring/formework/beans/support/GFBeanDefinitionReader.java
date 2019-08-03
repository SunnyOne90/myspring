package com.gaofeng.spring.formework.beans.support;

import com.gaofeng.spring.formework.beans.config.GFBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 读取bean相关的配置信息，将bean的配置信息解析成beanDefinition
 */
public class GFBeanDefinitionReader {

    private List<String> registyBeanClasses = new ArrayList<String>();

    Properties config = new Properties();

    //固定配置文件中的key，相对于xml的规范
    private final String SCAN_PACKAGE = "scanPackage";

    public GFBeanDefinitionReader(String... locations){
        //定位相关配置文件，并将其保存到properties
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:",""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    //提出bean的全限定名
    private void doScanner(String scanPackage) {
        URL url = this.getClass().getResource("/"+scanPackage.replaceAll("\\.","/"));
        //将url转换成file文件
        File classPath = new File(url.getFile());
        for (File file:classPath.listFiles()){
            if(file.isDirectory()){
                doScanner(scanPackage+ "." +file.getName());
            }else {
                if(!file.getName().endsWith(".class"))continue;
                String className = (scanPackage + "." + file.getName().replace(".class",""));
                registyBeanClasses.add(className);
            }
        }
    }
    public Properties getConfig(){
        return this.config;
    }


    public List<GFBeanDefinition> loadBeanDefinitions(){
        List<GFBeanDefinition> result = new ArrayList<GFBeanDefinition>();
        try {
            for (String className : registyBeanClasses) {
                //将className转换为class对象
                Class<?> beanClass = Class.forName(className);
                //判断是否为接口，如果是接口那么跳过
                if(beanClass.isInterface())continue;
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()),beanClass.getName()));

                Class<?>[] interfaces = beanClass.getInterfaces();

                for (Class<?> i : interfaces){
                    result.add(doCreateBeanDefinition(i.getName(),beanClass.getName()));
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    //将每一个配置信息转换为beanDefinition
    private GFBeanDefinition doCreateBeanDefinition(String factoryBeanName,String beanClassName){
        GFBeanDefinition beanDefinition = new GFBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }


    private String toLowerFirstCase(String simpleName){
        char [] chars = simpleName.toCharArray();
        chars[0]+=32;
        return String.valueOf(chars);
    }
}
