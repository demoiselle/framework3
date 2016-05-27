package org.demoiselle.rest.exception;

import static javax.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE;

/**
 * Created by 01748913506 on 24/05/16.
 */
public class ServiceUnavailableException extends HttpViolationException {

    private static final long serialVersionUID = 1L;

    public ServiceUnavailableException() {
        super(SC_SERVICE_UNAVAILABLE);
    }
}
