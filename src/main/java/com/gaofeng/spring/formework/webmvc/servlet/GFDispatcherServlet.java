package com.gaofeng.spring.formework.webmvc.servlet;

import com.gaofeng.spring.formework.annotation.GFController;
import com.gaofeng.spring.formework.annotation.GFRequestMapping;
import com.gaofeng.spring.formework.context.GFApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class GFDispatcherServlet extends HttpServlet {

    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";
    private final List<GFHandlerMapping> handlerMappings = new ArrayList<GFHandlerMapping>();
    private final Map<GFHandlerMapping,GFHandlerAdapter> handlerAdapterMap = new ConcurrentHashMap<GFHandlerMapping,GFHandlerAdapter>();
    private List<GFViewResolver> viewResolvers = new ArrayList<GFViewResolver>();
    private GFApplicationContext context;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp){
        try {
            this.doDispatch(req,resp);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        //根据用户请求的URL 来获得一个Handler
        GFHandlerMapping handler = getHandler(req);
        if(handler == null){
            processDispatchResult(req,resp,new GFModelAndView("404"));
            return;
        }
        //2、准备调用前的参数
        GFHandlerAdapter ha = getHandlerAdapter(handler);
        //3、真正的调用方法,返回ModelAndView存储了要穿页面上值，和页面模板的名称
        GFModelAndView mv = ha.handle(req,resp,handler);
        //4.这一步才是真正的输出
        processDispatchResult(req, resp, mv);
    }

    private GFHandlerAdapter getHandlerAdapter(GFHandlerMapping handler) {
        if(handlerAdapterMap.isEmpty()){return null;}
        GFHandlerAdapter handlerAdapter = handlerAdapterMap.get(handler);
        if(handlerAdapter.supports(handler)){
            return handlerAdapter;
        }
        return null;

    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, GFModelAndView modelAndView)throws Exception {
        if(modelAndView == null)return;
        if(viewResolvers.isEmpty())return;
        for (GFViewResolver viewResolver : viewResolvers) {
            GFView view = viewResolver.resolveViewName(modelAndView.getViewName(),null);
            view.render(modelAndView.getModel(),req,resp);
            return;
        }


    }

    /**
     * 通过request中的url获取指定的handler
     * @param req
     * @return
     */
    private GFHandlerMapping getHandler(HttpServletRequest req) {
//        String urlPath  = req.getContextPath();//返回系统项目名称
//        String uri = req.getRequestURI();//返回全路径
        String url = req.getRequestURI();;//返回全路径
        String contextPath = req.getContextPath();//返回系统项目名称
        url = url.replace(contextPath,"").replaceAll("/+","/");
        for (GFHandlerMapping handler : this.handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            if(!matcher.matches()){
                continue;
            }
            return handler;
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1.初始化applicationContext
        context = new GFApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        //2.初始化springMVC九大组件
        initStrategies(context);
    }

    protected void initStrategies(GFApplicationContext context) {
        //多文件上传的组件
        initMultipartResolver(context);
        //初始化本地语言环境
        initLocaleResolver(context);
        //初始化模板处理器
        initThemeResolver(context);
        //handlerMapping，必须实现
        initHandlerMappings(context);
        //初始化参数适配器，必须实现
        initHandlerAdapters(context);
        //初始化异常拦截器
        initHandlerExceptionResolvers(context);
        //初始化视图预处理器
        initRequestToViewNameTranslator(context);
        //初始化视图转换器，必须实现
        initViewResolvers(context);
        //参数缓存器
        initFlashMapManager(context);
    }

    private void initFlashMapManager(GFApplicationContext context) {
    }

    private void initViewResolvers(GFApplicationContext context) {
        //获取配置文件中的相关信息
        String templateRoot = context.getconfig().getProperty("templateRoot");
        //拿到模板的全路径
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);

        for (int i=0;i<templateRootDir.length();i++){
            this.viewResolvers.add(new GFViewResolver(templateRoot));
        }


    }

    private void initRequestToViewNameTranslator(GFApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(GFApplicationContext context) {
    }

    private void initHandlerAdapters(GFApplicationContext context) {
        for (GFHandlerMapping handlerMapping : this.handlerMappings) {
            handlerAdapterMap.put(handlerMapping,new GFHandlerAdapter());
        }

    }

    private void initHandlerMappings(GFApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();
        //遍历bean相关的信息
        for (String beanName : beanNames) {
            try {
                //向容器中获取bean的对象
                Object controller = context.getBean(beanName);
                //拿到这个对象的class对象
                Class<?> clazz = controller.getClass();
                String baseUrl = null;
                //判断这个实例是否存在controller注解
                if(!clazz.isAnnotationPresent(GFController.class))continue;
                //获取controller中url的配置信息
                if(clazz.isAnnotationPresent(GFRequestMapping.class)){
                    GFRequestMapping requestMapping = clazz.getAnnotation(GFRequestMapping.class);
                    baseUrl = requestMapping.value();
                }
                //遍历class中的所有方法
                for (Method method : clazz.getMethods()) {
                    //判断method上是否有GPRequestMapping注解
                    if(!method.isAnnotationPresent(GFRequestMapping.class)){continue;}
                    //获取method的url配置
                    GFRequestMapping methodRequestMapping = method.getAnnotation(GFRequestMapping.class);
                    //将controller的GPRequestMapping中的值与 method中GPRequestMapping的值进行拼接
                    String rex = ("/"+baseUrl + "/"+methodRequestMapping.value().trim().replaceAll("\\*",".*")).replaceAll("//","/");
                    Pattern pattern = Pattern.compile(rex);
                    //将HandlerMapping的相关信息通过list进行保存
                    this.handlerMappings.add(new GFHandlerMapping(pattern,controller,method));
                    log.info("Mapped " + rex + "," + method);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initThemeResolver(GFApplicationContext context) {
    }

    private void initLocaleResolver(GFApplicationContext context) {
    }

    private void initMultipartResolver(GFApplicationContext context) {
    }

}
