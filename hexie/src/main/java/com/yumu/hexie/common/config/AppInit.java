package com.yumu.hexie.common.config;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

public class AppInit implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        WebApplicationContext context = getContext();
        servletContext.addListener(new ContextLoaderListener(context));
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("DispatcherServlet", new DispatcherServlet(context));
        dispatcher.setLoadOnStartup(1);

        configureEncodingFilter(servletContext);
        configSpringSessionRepositoryFilter(servletContext, context);
        dispatcher.setAsyncSupported(true);
    }

    private void configSpringSessionRepositoryFilter(ServletContext servletContext, WebApplicationContext rootContext) {
        FilterRegistration.Dynamic session = servletContext.addFilter("springSessionRepositoryFilter",
                new DelegatingFilterProxy("springSessionRepositoryFilter", rootContext));
        session.addMappingForUrlPatterns(null, true, "/*");
    }
    private AnnotationConfigWebApplicationContext getContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation("com.yumu.hexie.common.config");
        return context;
    }

    private void configureEncodingFilter(ServletContext servletContext) {
        FilterRegistration.Dynamic fr = servletContext.addFilter("encodingFilter", new CharacterEncodingFilter());
        fr.setInitParameter("encoding", "UTF-8");
        fr.setInitParameter("forceEncoding", "true");
        fr.addMappingForUrlPatterns(null, true, "/*");
    }
}