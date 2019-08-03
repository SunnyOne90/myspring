package com.gaofeng.spring.formework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GFView {

    private File viewFile;

    public GFView(File viewFile) {
        this.viewFile = viewFile;
    }

    /**
     * 读取模板的相关信息，并且将制定位置进行替换
     * @param model
     * @param request
     * @param response
     * @throws Exception
     */
    public void render(Map<String, ?> model,
                       HttpServletRequest request, HttpServletResponse response) throws Exception{
        StringBuffer sb = new StringBuffer();
        RandomAccessFile raf = new RandomAccessFile(this.viewFile,"r");
        String line = null;
        while (null != (line = raf.readLine())){
            line = new String(line.getBytes("ISO-8859-1"),"utf-8");
            //创建正则匹配
            Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}",Pattern.CASE_INSENSITIVE);
            //开始比较
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()){
                //分组整个匹配
                String paramName = matcher.group();
                //将￥{}符号替换成""
                paramName = paramName.replaceAll("￥\\{|\\}","");
                //取出符号中的值
                Object paramValue = model.get(paramName);
                if(null == paramValue)continue;
                //对指定位置的字符进行替换
                line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                //将line返回给matcher继续比较
                matcher = pattern.matcher(line);
            }
            sb.append(line);
        }
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(sb.toString());
    }


    //处理特殊字符
    public static String makeStringForRegExp(String str) {
        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }
}
