package org.example;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.example.config.AppConfig;
import org.example.config.WebConfig;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

public class Main {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws Exception {
        Tomcat tomcat = startTomcat(DEFAULT_PORT);
        System.out.println("Server started on http://localhost:" + DEFAULT_PORT);
        tomcat.getServer().await();
    }

    static Tomcat startTomcat(int port) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector();

        Context context = tomcat.addContext("", new File(".").getAbsolutePath());

        AnnotationConfigWebApplicationContext springContext =
                new AnnotationConfigWebApplicationContext();
        springContext.register(AppConfig.class, WebConfig.class);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(springContext);

        Tomcat.addServlet(context, "dispatcher", dispatcherServlet);
        context.addServletMappingDecoded("/", "dispatcher");

        tomcat.start();
        return tomcat;
    }
}