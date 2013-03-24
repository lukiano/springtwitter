package com.lucho;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

public final class SimpleContextLoaderListener extends ContextLoaderListener {
	
	private final WebApplicationContext parentContext;

	public SimpleContextLoaderListener(final WebApplicationContext aParent, final WebApplicationContext context) {
		super(context);
		this.parentContext = aParent;
	}

	@Override
	public WebApplicationContext initWebApplicationContext(final ServletContext servletContext) {
		WebApplicationContext wac = super.initWebApplicationContext(servletContext);
		//servletContext.removeAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		return wac;
	}

	@Override
	protected ApplicationContext loadParentContext(final ServletContext servletContext) {
		return parentContext;
	}
}
