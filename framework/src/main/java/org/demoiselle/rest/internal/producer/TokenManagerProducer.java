package org.demoiselle.rest.internal.producer;

import org.demoiselle.annotation.Strategy;
import org.demoiselle.internal.producer.StrategySelector;
import org.demoiselle.rest.security.TokenManager;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

/**
 * Created by 01748913506 on 24/05/16.
 */
@Default
public class TokenManagerProducer {

    @Produces
    @Strategy
    public TokenManager create() {
        return StrategySelector.selectReference(TokenManager.class, null);
    }
}
