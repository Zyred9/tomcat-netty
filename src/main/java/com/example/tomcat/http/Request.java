package com.example.tomcat.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 * @className: Request
 * @projectName: tomcat
 * @author: Zyred
 * @date: 2019/11/28 14:57
 */
public class Request {

    private HttpRequest req;

    private ChannelHandlerContext ctx;

    public String getUrl() { return req.uri();}

    public String getMethod() { return req.method().name();}

    /**
     * 构造器初始化
     * @param ctx
     * @param req
     */
    public Request(ChannelHandlerContext ctx, HttpRequest req) {
        this.req = req;
        this.ctx = ctx;
    }

    /**
     * @param key
     * @return
     */
    public String getParam(String key){
        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        Map<String, List<String>> parameters = decoder.parameters();
        List<String> para = parameters.get(key);
        return para != null ? para.get(0) : null;
    }
}
