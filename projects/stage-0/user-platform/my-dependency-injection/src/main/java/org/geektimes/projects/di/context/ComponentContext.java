package org.geektimes.projects.di.context;

import javax.servlet.ServletContext;
import java.util.List;

public interface ComponentContext {
    static String CONTEXT_NAME = null;

    static ServletContext servletContext = null;

    /**
     * 获取 ComponentContext
     *
     * @return
     */
    static Object getInstance() {
        return servletContext.getAttribute(CONTEXT_NAME);
    }

    void init(ServletContext servletContext) throws RuntimeException;

    <C> C getComponent(String name);

    List<String> getComponentNames();

    void destroy() throws RuntimeException;
}
