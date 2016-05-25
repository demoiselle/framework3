package org.demoiselle.rest.security;

import org.demoiselle.annotation.literal.NameQualifier;
import org.demoiselle.internal.implementation.SecurityContextImpl;
import org.demoiselle.security.AuthenticationException;
import org.demoiselle.security.InvalidCredentialsException;
import org.demoiselle.security.SecurityContext;
import org.demoiselle.util.Strings;

import static java.util.logging.Level.FINE;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 01748913506 on 25/05/16.
 */
public abstract class AbstractHTTPAuthorizationFilter implements Filter {

    // private transient ResourceBundle bundle;

    private transient Logger logger;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (isActive() && isSupported(request)) {
            try {
                performLogin(request, response);
                chain.doFilter(request, response);
                performLogout(request, response);

            } catch (AuthenticationException cause) {
                // String message = getBundle().getString(cause.getMessage());
                getLogger().log(FINE, cause.getMessage(), cause);
                setUnauthorizedStatus(response, cause);
            }

        } else {
            chain.doFilter(request, response);
        }
    }

    protected String getAuthHeader(HttpServletRequest request) {
        String value = null;

        for (final Enumeration<String> names = request.getHeaderNames(); names.hasMoreElements();) {
            String name = names.nextElement();

            if ("authorization".equalsIgnoreCase(name)) {
                value = request.getHeader(name);
                break;
            }
        }

        return value;
    }

    protected String getAuthData(HttpServletRequest request) throws InvalidCredentialsException {
        String authData = null;
        String authHeader = getAuthHeader(request);
        String type = getType();

        if (!Strings.isEmpty(type) && !Strings.isEmpty(authHeader)) {
            String regexp = "^" + type + "[ \\n]+(.+)$";
            Pattern pattern = Pattern.compile(regexp, CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(authHeader);

            if (matcher.matches()) {
                authData = matcher.group(1);
            }
        }

        return authData;
    }

    protected boolean isSupported(HttpServletRequest request) {
        String data = getAuthData(request);
        return !Strings.isEmpty(data);
    }

    protected abstract boolean isActive();

    protected abstract String getType();

    protected void performLogin(HttpServletRequest request, HttpServletResponse response) {
        CDI.current().select(SecurityContext.class).get().login();
    }

    protected void performLogout(HttpServletRequest request, HttpServletResponse response) {
        if (CDI.current().select(SecurityContext.class).get().isLoggedIn()) {
            CDI.current().select(SecurityContext.class).get().logout();
        }
    }

    private void setUnauthorizedStatus(HttpServletResponse response, AuthenticationException cause) throws IOException {
        response.setStatus(SC_UNAUTHORIZED);

        String message = cause.getMessage();
        if (!Strings.isEmpty(message)) {
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().write(message);
        }
    }

    private Logger getLogger() {
        if (logger == null) {
            logger = CDI.current().select(Logger.class, new NameQualifier("org.demoiselle.security")).get();
        }

        return logger;
    }
}

