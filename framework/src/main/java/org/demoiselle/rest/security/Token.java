package org.demoiselle.rest.security;

import javax.enterprise.context.RequestScoped;

/**
 * Created by 01748913506 on 24/05/16.
 */
@RequestScoped
public class Token {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isEmpty() {
        return this.value == null;
    }

    public void clear() {
        this.value = null;
    }
}
