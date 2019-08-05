package com.gaofeng.spring.demo.action;



import com.gaofeng.spring.demo.service.IModifyService;
import com.gaofeng.spring.demo.service.IQueryService;
import com.gaofeng.spring.formework.annotation.GFAutowired;
import com.gaofeng.spring.formework.annotation.GFController;
import com.gaofeng.spring.formework.annotation.GFRequestMapping;
import com.gaofeng.spring.formework.annotation.GFRequestParam;
import com.gaofeng.spring.formework.webmvc.servlet.GFModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 公布接口url
 * @author Tom
 *
 */
@GFController
@GFRequestMapping("/web")
public class MyAction {

	@GFAutowired
	private IQueryService queryService;

	@GFAutowired
    private IModifyService modifyService;

	@GFRequestMapping("/query.json")
	public GFModelAndView query(HttpServletRequest request, HttpServletResponse response,
                                @GFRequestParam("name") String name){
		String result = queryService.query(name);
		return out(response,result);
	}

	@GFRequestMapping("/add*.json")
	public GFModelAndView add(HttpServletRequest request, HttpServletResponse response,
                              @GFRequestParam("name") String name, @GFRequestParam("addr") String addr){
		String result = null;
		try {
			result = modifyService.add(name,addr);
			return out(response,result);
		} catch (Exception e) {
//			e.printStackTrace();
			Map<String,Object> model = new HashMap<String,Object>();
			model.put("detail",e.getCause().getMessage());
//			System.out.println(Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
			model.put("stackTrace", Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
			return new GFModelAndView("500",model);
		}

	}

	@GFRequestMapping("/remove.json")
	public GFModelAndView remove(HttpServletRequest request, HttpServletResponse response,
                                 @GFRequestParam("id") Integer id){
		String result = modifyService.remove(id);
		return out(response,result);
	}

	@GFRequestMapping("/edit.json")
	public GFModelAndView edit(HttpServletRequest request, HttpServletResponse response,
                               @GFRequestParam("id") Integer id,
                               @GFRequestParam("name") String name){
		String result = modifyService.edit(id,name);
		return out(response,result);
	}



	private GFModelAndView out(HttpServletResponse resp, String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
