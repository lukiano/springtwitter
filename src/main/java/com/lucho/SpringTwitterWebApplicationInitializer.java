package com.lucho;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.atmosphere.cpr.MeteorServlet;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextCleanupListener;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * @author Luciano.Leggieri
 */
public final class SpringTwitterWebApplicationInitializer implements
        WebApplicationInitializer {

    /**
     * Websocket timeout in milliseconds.
     */
    private static final long WEBSOCKET_INACTIVITY_TIMEOUT_IN_MILLIS =
            TimeUnit.MINUTES.toMillis(30);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStartup(final ServletContext servletContext)
            throws ServletException {

        XmlWebApplicationContext rootWac = new XmlWebApplicationContext(null);
        //ContextLoaderListener rootCll = new SimpleContextLoaderListener(null, rootWac);
        //servletContext.addListener(rootCll);
        rootWac.setServletContext(servletContext);
        rootWac.refresh();
        
    	XmlWebApplicationContext repositoryWac = new XmlWebApplicationContext(rootWac);
    	repositoryWac.setNamespace("jpa-config");
        repositoryWac.setServletContext(servletContext);
        repositoryWac.refresh();
//    	ContextLoaderListener repositoryCll = new SimpleContextLoaderListener(rootWac, repositoryWac);
//    	servletContext.addListener(repositoryCll);

    	XmlWebApplicationContext securityWac = new XmlWebApplicationContext(repositoryWac);
    	securityWac.setNamespace("security-config");
        securityWac.setServletContext(servletContext);
        securityWac.refresh();
//    	ContextLoaderListener securityCll = new SimpleContextLoaderListener(repositoryWac, securityWac);
//    	servletContext.addListener(securityCll);

    	XmlWebApplicationContext integrationWac = new XmlWebApplicationContext(securityWac);
    	integrationWac.setNamespace("integration-config");
        integrationWac.setServletContext(servletContext);
        integrationWac.refresh();
//    	ContextLoaderListener integrationCll = new SimpleContextLoaderListener(securityWac, integrationWac);
//    	servletContext.addListener(integrationCll);

    	ContextLoaderListener rootCll = new SimpleContextLoaderListener(securityWac, integrationWac);
    	servletContext.addListener(rootCll);

        servletContext.addListener(HttpSessionEventPublisher.class);
        servletContext.addListener(ContextCleanupListener.class);

    	XmlWebApplicationContext servletWac = new XmlWebApplicationContext(integrationWac);
        servletWac.setNamespace("tweeter-servlet");

        if (rootWac.getEnvironment().acceptsProfiles("websockets")) {
            Servlet dispatcherServlet = new DispatcherServlet(servletWac);
            MeteorServlet meteorServlet = new MeteorServlet(dispatcherServlet);
            meteorServlet.setFilterClassName("com.lucho.util.SSFilter");

            ServletRegistration.Dynamic dispatcher = servletContext.addServlet("tweeter", meteorServlet);
            dispatcher.setInitParameter("org.atmosphere.cpr.broadcasterClass",
                    "org.atmosphere.cpr.DefaultBroadcaster");
            dispatcher.setInitParameter("org.atmosphere.cpr.CometSupport.maxInactiveActivity",
                    Long.toString(WEBSOCKET_INACTIVITY_TIMEOUT_IN_MILLIS));
            dispatcher.setInitParameter("org.atmosphere.useStream", FALSE.toString());
            dispatcher.setInitParameter("org.atmosphere.useWebSocket", TRUE.toString());
            dispatcher.setInitParameter("org.atmosphere.useNative", TRUE.toString());
            dispatcher.setInitParameter("org.atmosphere.cpr.broadcaster.shareableThreadPool", FALSE.toString());
            dispatcher.setInitParameter("org.atmosphere.cpr.broadcasterLifeCyclePolicy", "IDLE");

            dispatcher.setLoadOnStartup(1);
            dispatcher.addMapping("/");
        } else {
            FilterRegistration rcf = servletContext.addFilter(
                    "requestContextFilter", new DelegatingFilterProxy("requestContextFilter", securityWac));
            rcf.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), false, "/*");

            FilterRegistration ssfc = servletContext.addFilter(
                    "springSecurityFilterChain", new DelegatingFilterProxy("springSecurityFilterChain", securityWac));
            ssfc.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), false, "/*");

            ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
                    "tweeter", new DispatcherServlet(servletWac));
            dispatcher.setLoadOnStartup(1);
            dispatcher.addMapping("/");
        }

    }

}
