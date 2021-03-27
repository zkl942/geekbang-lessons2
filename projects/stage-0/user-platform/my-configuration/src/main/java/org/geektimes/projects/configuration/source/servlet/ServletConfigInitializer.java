package org.geektimes.projects.configuration.source.servlet;

import org.geektimes.projects.configuration.listener.ComponentContextInitializerListener;
import org.geektimes.projects.configuration.listener.ConfigServletRequestListener;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

/**
 * ServletContainerInitializer is based on Service Provider Interface (SPI) concept.
 * 主要功能就是在容器启动的时候，通过编程的方式去注册Servlet Filter Listenner等组件，代替web.xml
 */
public class ServletConfigInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) throws ServletException {
        // 增加 ServletContextListener
        servletContext.addListener(ServletContextConfigInitializer.class);
        servletContext.addListener(ComponentContextInitializerListener.class);
        servletContext.addListener(ConfigServletRequestListener.class);
    }
}
