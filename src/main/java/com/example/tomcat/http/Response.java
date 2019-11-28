package com.example.tomcat.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.IOException;
import java.util.Objects;

/**
 * @className: Response
 * @projectName: tomcat
 * @author: Zyred
 * @date: 2019/11/28 15:05
 */
public class Response {

    private HttpRequest request;

    private ChannelHandlerContext ctx;

    public Response(ChannelHandlerContext ctx, HttpRequest request) {
        this.request = request;
        this.ctx = ctx;
    }

    public void write(String s) {
        try {
        if(Objects.isNull(s)){
            throw new NullPointerException("Response result is not null.");
        }
        FullHttpResponse response = new DefaultFullHttpResponse(
                //设置Http版本
                HttpVersion.HTTP_1_1,
                //设置响应状态
                HttpResponseStatus.OK,
                //设置响应编码
                Unpooled.wrappedBuffer(s.getBytes("UTF-8"))
            );
        //设置响应头
        response.headers().set("Content-Type", "text/html;");
        ctx.write(response);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            ctx.flush();
            ctx.close();
        }
    }

}
