package org.demoiselle.rest.security;

import org.demoiselle.rest.internal.configuration.RESTSecurityConfig;
import org.demoiselle.security.InvalidCredentialsException;
import org.demoiselle.servlet.security.Credentials;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by 01748913506 on 25/05/16.
 */
public class BasicAuthFilter extends AbstractHTTPAuthorizationFilter {

    @Override
    protected String getType() {
        return "Basic";
    }

    @Override
    protected void performLogin(HttpServletRequest request, HttpServletResponse response) {
        String[] decoded = decodeCredentials(request);

        Credentials credentials = CDI.current().select(Credentials.class).get(); //Beans.getReference(Credentials.class);
        credentials.setUsername(decoded[0]);
        credentials.setPassword(decoded[1]);

        super.performLogin(request, response);
    }

    private String[] decodeCredentials(HttpServletRequest request) throws InvalidCredentialsException {
        String[] result = null;

        String authData = getAuthData(request);
        byte[] decoded = Base64.decodeBase64(authData);
        result = new String(decoded).split(":");

        if (result == null || result.length != 2) {
            throw new InvalidCredentialsException("formato inválido do cabeçalho");
        }

        return result;
    }

    @Override
    protected boolean isActive() {
        return CDI.current().select(RESTSecurityConfig.class).get().isBasicFilterActive();
    }
}

