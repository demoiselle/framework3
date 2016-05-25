package org.demoiselle.rest.security;

import java.security.Principal;

/**
 * Created by 01748913506 on 24/05/16.
 */
public interface TokenManager {
    String persist(Principal user) throws Exception;

    Principal load(String token) throws Exception;
}
