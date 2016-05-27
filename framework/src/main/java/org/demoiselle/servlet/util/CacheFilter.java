package org.demoiselle.servlet.util;

import org.demoiselle.util.Strings;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by 01748913506 on 24/05/16.
 */
public class CacheFilter implements Filter {

    private String value;

    @Override
    public void init(FilterConfig config) throws ServletException {
        value = config.getInitParameter("value");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            setCacheControl((HttpServletResponse) response);
        }

        chain.doFilter(request, response);
    }

    protected void setCacheControl(HttpServletResponse response) {
        if (Strings.isEmpty(response.getHeader("Cache-Control"))) {
            response.setHeader("Cache-Control", value);
        }
    }

    @Override
    public void destroy() {
    }
}