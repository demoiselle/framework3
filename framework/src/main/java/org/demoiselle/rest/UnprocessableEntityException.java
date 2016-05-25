package org.demoiselle.rest;

import org.demoiselle.rest.HttpViolationException;

/**
 * Created by 01748913506 on 25/05/16.
 */
public class UnprocessableEntityException extends HttpViolationException {

    private static final long serialVersionUID = 1L;

    public UnprocessableEntityException() {
        super(422);
    }
}