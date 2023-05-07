package com.example.demo.app.v1.servlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServlet;
import java.lang.reflect.Field;

public abstract class SpringInjectedHttpServlet extends HttpServlet {

    protected <T> T getSpringBean(Class<T> clazz) {
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        return ctx.getBean(clazz);
    }

    protected void injectAutowiredBeans() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                field.setAccessible(true);
                try {
                    field.set(this, getSpringBean(field.getType()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void init() {
        injectAutowiredBeans();
    }
}
