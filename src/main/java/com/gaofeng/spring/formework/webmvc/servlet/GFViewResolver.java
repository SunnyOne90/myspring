package com.gaofeng.spring.formework.webmvc.servlet;

import java.io.File;
import java.util.Locale;

public class GFViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFX = ".html";

    private File templateRootDir;

    public GFViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);
    }
    public GFView resolveViewName(String viewName, Locale locale) throws Exception{
        if(null == viewName || "".equals(viewName.trim()))return null;
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFX)? viewName : (viewName + DEFAULT_TEMPLATE_SUFFX);
        File templateFile = new File((templateRootDir.getPath() + "/"+viewName).replaceAll("/+","/"));
        return new GFView(templateFile);
    }

}
