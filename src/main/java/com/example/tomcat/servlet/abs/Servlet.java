package com.example.tomcat.servlet.abs;

import com.example.tomcat.http.Request;
import com.example.tomcat.http.Response;

/**
 *
 * @className: Servlet
 * @projectName: tomcat
 * @author: Zyred
 * @date: 2019/11/28 15:07
 */
public abstract class Servlet {

    private final String DEFAULT_METHOD_GET = "GET";
    private final String DEFAULT_METHOD_POST = "POST";

    public void service(Request req, Response resp){
        if(DEFAULT_METHOD_GET.equalsIgnoreCase(req.getMethod())){
            doGet(req, resp);
        }
        else if (DEFAULT_METHOD_POST.equalsIgnoreCase(req.getMethod())){
            doPost(req, resp);
        }
    }

    /**
     * post方法执行逻辑
     * @param req
     * @param resp
     */
    protected abstract void doPost(Request req, Response resp);

    /**
     * get方法，执行逻辑
     * @param req
     * @param resp
     */
    public abstract void doGet(Request req, Response resp);


}
