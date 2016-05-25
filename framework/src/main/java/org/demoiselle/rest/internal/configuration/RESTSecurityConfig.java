package org.demoiselle.rest.internal.configuration;

import org.demoiselle.annotation.Name;
import org.demoiselle.configuration.Configuration;

import java.io.Serializable;

/**
 * Created by 01748913506 on 25/05/16.
 */
@Configuration(prefix = "demoiselle.security")
public class RESTSecurityConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Name("basic.filter.active")
    private boolean basicFilterActive = true;

    @Name("token.filter.active")
    private boolean tokenFilterActive = true;

    public boolean isBasicFilterActive() {
        return basicFilterActive;
    }

    public boolean isTokenFilterActive() {
        return tokenFilterActive;
    }
}

