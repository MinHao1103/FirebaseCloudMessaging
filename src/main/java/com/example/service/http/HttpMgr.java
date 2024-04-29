package com.example.service.http;

import com.example.service.filter.MyFilter;
import com.example.service.http.utils.log.Log;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.reflections.Reflections;

import javax.servlet.DispatcherType;
import javax.ws.rs.Path;
import java.util.EnumSet;
import java.util.Set;

public class HttpMgr {
    private static final String TAG = "HttpMgr";
    private static final int httpServerPort = 18080;
    private Server server;
    static HttpMgr instance;

    public static HttpMgr getInstance() { // 整個應用程式中止會有一個 HttpMgr 實例存在
        if (instance == null) {
            instance = new HttpMgr();
        }
        return instance;
    }

    public HttpMgr() {
    }

    public void init() {
        try {
            Log.d(TAG, "HttpServerPort : " + httpServerPort);

            // TODO 1. 建立 Jetty Server
            server = new Server();

            // TODO 2. 建立 Jetty Connector
            HttpConfiguration httpConfiguration = new HttpConfiguration();
            ServerConnector serverConnector = new ServerConnector(server, new HttpConnectionFactory(httpConfiguration));
            serverConnector.setPort(httpServerPort); // 連接器的 Port
            serverConnector.setIdleTimeout(3000); // 連接的閒置時間超過 30 秒後自動關閉
            serverConnector.setAcceptQueueSize(50); // 連接器的最大數量
            server.addConnector(serverConnector);

            // TODO 3. 建立 Jetty Handler
            ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS); // NO_SESSIONS，表示每個 HTTP 請求都將是無狀態
            contextHandler.setContextPath("/");

            // 加入過濾器
            contextHandler.addFilter(MyFilter.class, "/*", EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));

            ResourceConfig resourceConfig = new ResourceConfig(); // 建立 Jersey 的 ResourceConfig

            Log.d(TAG, "Using Reflections to find classes annotated with @Path.");
            Set<Class<?>> classes = new Reflections().getTypesAnnotatedWith(Path.class);
            for (Class<?> clazz : classes) {
                String packageName = clazz.getPackage().getName();
                resourceConfig.packages(packageName); // 告訴 Jersey 掃描處理類別
            }
            Log.d(TAG, "Finished finding classes annotated  with @Path using Reflections.");

            // 建立 Jersey 的 ServletHolder 配置 ResourceConfig，讓 Jersey 處理 /HTTPServer/* 開頭的 URL
            contextHandler.addServlet(new ServletHolder(new ServletContainer(resourceConfig)), "/HTTPServer/*");
            server.setHandler(contextHandler); // 將配置好的 contextHandler 加入 server

            // TODO 4. 啟動 Jetty Server
            startJettyServer();
        } catch (Exception e) {
            Log.d(TAG, "HttpMgr initialization failed.");
            e.printStackTrace();
        }
    }

    public void startJettyServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server.start();
                    Log.d(TAG, "Server started");
                    server.join();
                } catch (Exception e) {
                    Log.e(TAG, "Error while running server: " + e.getMessage());
                } finally {
                    try {
                        if (server != null && server.isRunning()) {
                            server.stop();
                            Log.d(TAG, "Server stopped");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error while stopping server: " + e.getMessage());
                    }
                }
            }
        }).start();
    }

}
