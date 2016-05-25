package org.demoiselle.rest;

import org.demoiselle.rest.HttpViolationException;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 * Created by 01748913506 on 25/05/16.
 */
public class UnauthorizedException extends HttpViolationException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedException() {
        super(SC_UNAUTHORIZED);
    }

}
