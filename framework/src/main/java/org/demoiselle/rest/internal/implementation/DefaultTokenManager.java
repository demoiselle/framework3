package org.demoiselle.rest.internal.implementation;

import org.demoiselle.annotation.Priority;
import org.demoiselle.rest.security.TokenManager;

import javax.enterprise.context.ApplicationScoped;
import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by 01748913506 on 25/05/16.
 */
@ApplicationScoped
@Priority(Priority.L2_PRIORITY)
public class DefaultTokenManager implements TokenManager {

    private TokenStore store = new TokenStore();

    @Override
    public String persist(Principal user) throws Exception {
        return store.put(user);
    }

    @Override
    public Principal load(String token) throws Exception {
        return store.get(token);
    }

    public static class TokenStore implements Serializable {

        private static final long serialVersionUID = 1L;

        private Map<String, Principal> map = Collections.synchronizedMap(new HashMap<String, Principal>());

        public String put(Principal user) {
            String token = UUID.randomUUID().toString();

            if (map.containsValue(user)) {
                map.remove(token);
            }

            map.put(token, user);

            return token;
        }

        public Principal get(String token) {
            return map.get(token);
        }
    }
}

