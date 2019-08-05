package com.gaofeng.spring.formework.aop.support;

import com.gaofeng.spring.formework.aop.aspect.GFAfterReturningAdviceInterceptor;
import com.gaofeng.spring.formework.aop.aspect.GFAfterThrowingAdviceInterceptor;
import com.gaofeng.spring.formework.aop.aspect.GFMethodBeforeAdviceInterceptor;
import com.gaofeng.spring.formework.aop.config.GFAopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GFAdvisedSupport {
    private GFAopConfig config;
    //实例化类的class对象
    private Class<?> targetClass;
    //实例化的类
    private Object target;

    private Pattern pointCutClassPattern;


    private transient Map<Method, List<Object>> methodCache;

    public GFAdvisedSupport(GFAopConfig config) {
        this.config = config;
    }

    public boolean pointCutMatch(){
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Object getTarget() {
        return target;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    private void parse() {
        //将pointCut表达式替换成java正则的方式以便匹配
        String pointCut = config.getPointCut()
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\\\(")
                .replaceAll("\\)","\\\\)");
        //pointCut=public .* com.gupaoedu.vip.spring.demo.service..*Service..*(.*)
        //玩正则
        //com.gupaoedu.vip.spring.demo.service..*Service截取成这种形式
        String pointCutForClassRegex = pointCut.substring(0,pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(
                pointCutForClassRegex.lastIndexOf(" ") + 1));
        try{
            //用于缓存目标方法与责任链的相关数据
            methodCache = new HashMap<Method, List<Object>>();
            //创建正则
            Pattern pattern = Pattern.compile(pointCut);
            Class aspectClass = Class.forName(this.config.getAspectClass());
            Map<String,Method> aspectMethods = new HashMap<String, Method>();
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(),method);
            }
            for (Method m : this.targetClass.getMethods()){
                String methodString = m.toString();
                if(methodString.contains("throws")){
                    methodString = methodString.substring(0,methodString.lastIndexOf("throws")).trim();
                }
                //通过方法的全定名和正则表达式进行匹配
                Matcher matcher = pattern.matcher(methodString);
                if(matcher.matches()){
                    List<Object> advices = new LinkedList<Object>();
                    //构建before拦截器链
                    if(!("".equals(config.getAspectBefore()) || null == config.getAspectBefore())){
                        advices.add(new GFMethodBeforeAdviceInterceptor(aspectMethods.get(config.getAspectBefore()),aspectClass.newInstance()));
                    }
                    //构建after拦截器链
                    if(!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))) {
                        //创建一个Advivce
                        advices.add(new GFAfterReturningAdviceInterceptor(aspectMethods.get(config.getAspectAfter()),aspectClass.newInstance()));
                    }
                    //构建AfterThrow拦截器链
                    if(!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))){
                        GFAfterThrowingAdviceInterceptor throwingAdvice =
                                new GFAfterThrowingAdviceInterceptor
                                        (aspectMethods.get(config.getAspectAfterThrow()),aspectClass.newInstance());
                        throwingAdvice.setThrowName(config.getAspectAfterThrowingName());
                        advices.add(throwingAdvice);

                    }
                    methodCache.put(m,advices);
                }
            }

            /**
             * 实例化切面类，将方法名以及方法缓存到map中
             * 遍历目标类的方法，将方法上的异常截掉
             * 通过方法的全定名和正则表达式进行匹配
             * 如果匹配到了，那么构建这个拦截器链
             * 创建拦截器链缓存
             * 如果存在before的配置，那么构建befor的拦截器链
             * 一下以此类推
             */
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass)throws Exception {
        //向缓存获取一个拦截器链
        List<Object> cached = methodCache.get(method);
        if(cached == null){
            //通过被代理的对象获取指定的方法
            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());
            cached = methodCache.get(m);
            this.methodCache.put(m,cached);
        }
        return cached;

    }
}
