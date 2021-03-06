package org.xdi.oxd.server.service;

import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.oxd.server.Configuration;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 12/09/2016
 */

public class StateService {

    private static final Logger LOG = LoggerFactory.getLogger(StateService.class);

    private final Cache<String, String> states;
    private final Cache<String, String> nonces;

    private final SecureRandom random = new SecureRandom();

    @Inject
    public StateService(ConfigurationService configurationService) {

        Configuration conf = configurationService.get();

        states = CacheBuilder.newBuilder()
                .expireAfterWrite(conf.getStateExpirationInMinutes(), TimeUnit.MINUTES)
                .build();
        nonces = CacheBuilder.newBuilder()
                .expireAfterWrite(conf.getNonceExpirationInMinutes(), TimeUnit.MINUTES)
                .build();

    }

    public String generateState() {
        return putState(generateSecureString());
    }

    public String generateNonce() {
        return putNonce(generateSecureString());
    }

    private String generateSecureString() {
        return new BigInteger(130, random).toString(32);
    }

    public boolean isStateValid(String state) {
        return !Strings.isNullOrEmpty(states.getIfPresent(state));
    }

    public boolean isNonceValid(String nonce) {
        return !Strings.isNullOrEmpty(nonces.getIfPresent(nonce));
    }

    public void invalidateState(String state) {
        states.invalidate(state);
    }

    public void invalidateNonce(String nonce) {
        nonces.invalidate(nonce);
    }

    public String putState(String state) {
        states.put(state, state);
        return state;
    }

    public String putNonce(String nonce) {
        nonces.put(nonce, nonce);
        return nonce;
    }
}
