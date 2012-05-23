package com.lucho.atmosphere;

import javax.servlet.Filter;

/**
 * Helper structure that holds a Servlet Filter and its name.
 * @author Luciano.Leggieri
 *
 */
public final class FilterAndName {

    /**
     * A servlet Filter.
     */
    private final Filter f;

    /**
     * The filter's name.
     */
    private final String name;

    /**
     * Class constructor.
     * @param filter A servlet filter.
     * @param filterName the filter's name.
     */
    public FilterAndName(final Filter filter, final String filterName) {
        f = filter;
        name = filterName;
    }

	public Filter getFilter() {
		return f;
	}

	public String getName() {
		return name;
	}

}
