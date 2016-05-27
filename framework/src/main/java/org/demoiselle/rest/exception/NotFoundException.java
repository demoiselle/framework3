package org.demoiselle.rest.exception;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * Created by 01748913506 on 24/05/16.
 */
public class NotFoundException extends HttpViolationException {

    private static final long serialVersionUID = 1L;

    public NotFoundException() {
        super(SC_NOT_FOUND);
    }
}

