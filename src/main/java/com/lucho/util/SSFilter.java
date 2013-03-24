package com.lucho.util;

import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Resource;
import javax.servlet.Filter;

@Configurable
public final class SSFilter extends AbstractFilterDecorator {

    private Filter delegate;

    protected Filter getDelegate() {
        return this.delegate;
    }

    @Resource(name = "springSecurityFilterChain")
    public void setDelegate(final Filter filter) {
        this.delegate = filter;
    }

}
