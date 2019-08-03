package com.gaofeng.spring.formework.webmvc.servlet;

import java.util.Map;

public class GFModelAndView {

    private String viewName;
    private Map<String,?> model;

    public GFModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public GFModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }
}
