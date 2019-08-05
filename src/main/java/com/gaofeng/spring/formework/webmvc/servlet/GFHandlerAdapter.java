package com.gaofeng.spring.formework.webmvc.servlet;

import com.gaofeng.spring.formework.annotation.GFRequestParam;
import com.sun.org.apache.regexp.internal.RE;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GFHandlerAdapter {

    public boolean supports(Object handler){
        return (handler instanceof GFHandlerMapping);
    }

    GFModelAndView handle(HttpServletRequest req, HttpServletResponse resp, Object handler)throws Exception {
        //将handler进行类型转换
        GFHandlerMapping handlerMapping = (GFHandlerMapping)handler;
        // 创建Map 记录方法中参数的位置及对应的坐标
        Map<String,Integer> paramIndexMapping = new HashMap<String, Integer>();
        //获取方法中所有的RequestParam注解信息,得到一个二维数组
        Annotation[] [] pa = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0;i<pa.length;i++){
            for (Annotation a : pa[i]){
                //遍历相关参数 判断类型是否为GPRequestParam
                if(a instanceof GFRequestParam){
                    String paramName = ((GFRequestParam) a).value();
                    if(!"".equals(paramName.trim())){
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }
        //记录request及response参数位置
        Class<?> [] paramsTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i=0;i<paramsTypes.length;i++){
            Class<?> type = paramsTypes[i];
            if(type == HttpServletRequest.class || type == HttpServletResponse.class){
                paramIndexMapping.put(type.getName(),i);
            }
        }
        Map<String ,String[]> params = req.getParameterMap();

        Object[] paramValues = new Object[paramsTypes.length];
        for (Map.Entry<String, String[]> parm : params.entrySet()) {
            String value = Arrays.toString(parm.getValue()).replaceAll("\\[|\\]","")
                    .replaceAll("\\s",",");
            if(!paramIndexMapping.containsKey(parm.getKey())){continue;}

            int index = paramIndexMapping.get(parm.getKey());
            paramValues[index] = caseStringValue(value,paramsTypes[index]);
        }
        if(paramIndexMapping.containsKey(HttpServletRequest.class.getName())){
            int index = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[index] = req;
        }
        if(paramIndexMapping.containsKey(HttpServletResponse.class.getName())){
            int index = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[index] = resp;
        }
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(),paramValues);
        if(result == null || result instanceof Void){ return null; }

        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == GFModelAndView.class;
        if(isModelAndView){
            return (GFModelAndView) result;
        }
        return null;
    }


    /**
     * 对参数的类型进行转换
     * @param value
     * @param paramsType
     * @return
     */
    private Object caseStringValue(String value, Class<?> paramsType){
        if(String.class == paramsType){
            return String.valueOf(value);
        }else if(Integer.class == paramsType){
            return Integer.valueOf(value);
        }else if(Double.class == paramsType){
            return Double.valueOf(value);
        }else {
            if(value != null){
                return value;
            }else {
                return null;
            }
        }
    }

}
