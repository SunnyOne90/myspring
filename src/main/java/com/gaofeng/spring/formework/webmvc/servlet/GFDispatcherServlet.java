package com.gaofeng.spring.formework.webmvc.servlet;

import com.gaofeng.spring.formework.context.GFApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GFDispatcherServlet extends HttpServlet {

    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doDispatch(req,resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1.初始化applicationContext
//        context = new GFApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
    }
}
