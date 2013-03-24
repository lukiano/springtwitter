package com.lucho.util;

import javax.servlet.*;
import java.io.IOException;

/**
 *
 */
public abstract class AbstractFilterDecorator implements Filter {

    protected abstract Filter getDelegate();

    public final void init(final FilterConfig filterConfig) throws ServletException {
        this.getDelegate().init(filterConfig);
    }

    public final void destroy() {
        this.getDelegate().destroy();
    }

    public final void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        this.getDelegate().doFilter(request, response, chain);
    }

}
