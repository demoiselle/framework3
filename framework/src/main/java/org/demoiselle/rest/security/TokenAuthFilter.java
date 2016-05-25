package org.demoiselle.rest.security;

import org.demoiselle.rest.internal.configuration.RESTSecurityConfig;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by 01748913506 on 25/05/16.
 */
public class TokenAuthFilter extends AbstractHTTPAuthorizationFilter {

    @Override
    protected String getType() {
        return "Token";
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String authData = getAuthData(request);

        super.doFilter(request, response, chain);

        String value = CDI.current().select(Token.class).get().getValue();
        if (value != null && !value.equals(authData)) {
            response.setHeader("Set-Token", value);
        }
    }

    @Override
    protected void performLogin(HttpServletRequest request, HttpServletResponse response) {
        Token token = CDI.current().select(Token.class).get();
        String authData = getAuthData(request);
        token.setValue(authData);

        super.performLogin(request, response);
    }

    @Override
    protected boolean isActive() {
        return CDI.current().select(RESTSecurityConfig.class).get().isTokenFilterActive();
    }
}

