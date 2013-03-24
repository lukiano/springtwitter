package com.lucho.util;

import javax.annotation.Resource;
import javax.servlet.Filter;

/**
 * Created with IntelliJ IDEA.
 * User: luciano
 * Date: 24/01/13
 * Time: 17:00
 * To change this template use File | Settings | File Templates.
 */
public final class RCFilter extends AbstractFilterDecorator {

    private Filter delegate;

    protected Filter getDelegate() {
        return this.delegate;
    }

    @Resource(name = "requestContextFilter")
    public void setDelegate(final Filter filter) {
        this.delegate = filter;
    }

}
