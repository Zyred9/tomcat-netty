package com.example.tomcat.servlet;

import com.example.tomcat.http.Request;
import com.example.tomcat.http.Response;
import com.example.tomcat.servlet.abs.Servlet;

/**
 * @className: ServletTwo
 * @projectName: tomcat
 * @author: Zyred
 * @date: 2019/11/28 15:18
 */
public class ServletTwo extends Servlet {
    @Override
    protected void doPost(Request req, Response resp) {
        doGet(req, resp);
    }

    @Override
    public void doGet(Request req, Response resp) {
        resp.write("This is ServletTwo response result");
    }
}
