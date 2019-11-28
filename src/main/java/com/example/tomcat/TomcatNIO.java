package com.example.tomcat;

import com.example.tomcat.http.Request;
import com.example.tomcat.http.Response;
import com.example.tomcat.servlet.abs.Servlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @className: Tomcat
 * @projectName: tomcat
 * @author: Zyred
 * @date: 2019/11/28 14:55
 */
public class TomcatNIO {

    private int port = 8080;

    public TomcatNIO() {}

    public TomcatNIO(int port) {
        this.port = port;
    }

    private ServerSocket socket;

    private Map<String, Servlet> servletMapping = new HashMap<>();

    private Properties properties = new Properties();

    private void init(){
        try {
            //获取当前项目根路径CLASSPATH
            String sourcePath = this.getClass().getResource("/").getPath();
            //读取配置文件
            FileInputStream fis = new FileInputStream(sourcePath + "web.properties");
            //加载配置文件
            properties.load(fis);

            //从配置文件中遍历rul，
            for (Object o : properties.keySet()) {
                String key = o.toString();
                if(key.endsWith(".url")){
                    //截取URL
                    String servletName = key.replaceAll("\\.url$", "");
                    String url = properties.getProperty(key);
                    String className = properties.getProperty(servletName + ".className");
                    //单实例，多线程
                    Servlet obj = (Servlet)Class.forName(className).newInstance();
                    servletMapping.put(url, obj);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void start(){
        init();
        System.out.println("服务已启动, 监听端口：" + port);
        //bioStart();
        nioStart();
    }

    /**
     * 使用BIO模型启动
     */
    private final void bioStart(){
        try {
            socket = new ServerSocket(this.port);
            while(true){
                Socket request = socket.accept();
                process(request);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 使用Netty启动
     */
    private final void nioStart(){
        //boos线程
        EventLoopGroup boos = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try{
            ServerBootstrap server = new ServerBootstrap();
            server.group(boos, worker).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpResponseEncoder());
                            ch.pipeline().addLast(new HttpRequestDecoder());
                            ch.pipeline().addLast(new TomcatHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .bind(port)
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            boos.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

    public class TomcatHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof HttpRequest){
                HttpRequest req = (HttpRequest) msg;
                // 转交给我们自己的request实现
                Request request = new Request(ctx,req);
                // 转交给我们自己的response实现
                Response response = new Response(ctx,req);
                // 实际业务处理
                String url = request.getUrl();
                if(servletMapping.containsKey(url)){
                    servletMapping.get(url).service(request, response);
                }else{
                    response.write("404 - Not Found");
                }

            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        }

    }

    private void process(Socket request) throws IOException {
        //通过Socket拿到请求/响应对象
        Request req = new Request(null, null);
        Response resp = new Response(null, null);
        //通过请求对象，获取到请求路径
        String url = req.getUrl();
        //验证是否有Key
        if(servletMapping.containsKey(url)){
            System.out.println("请求进入：" + url);
            //获取到key对应的Servlet并且执行service()
            servletMapping.get(url).service(req, resp);
        }else{
            //直接返回错误信息
            resp.write("404 - Not Found");
        }
    }
}

class TomcatTest{
    public static void main(String[] args) {
        new TomcatNIO().start();
    }
}
