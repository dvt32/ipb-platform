package com.ipb.platform.security;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * This class is responsible for counting 
 * the number of logins the user made,
 * setting the block time in minutes and also 
 * for blocking/unblocking the user
 * when the limits have been reached.
 * 
 * @author dvt32
 */
@Service
public class LoginAttemptService {
 
    private final int MAX_INVALID_LOGIN_ATTEMPTS = 3;
    private final int BLOCK_TIME_IN_MINUTES = 10;
    
    private LoadingCache<String, Integer> attemptsCache;
 
    public LoginAttemptService() {
        super();
        attemptsCache = CacheBuilder.newBuilder().
          expireAfterWrite(BLOCK_TIME_IN_MINUTES, TimeUnit.MINUTES).build(new CacheLoader<String, Integer>() {
            public Integer load(String key) {
                return 0;
            }
        });
    }
 
    public void loginSucceeded(String key) {
        if (!isBlocked(key)) {
        	attemptsCache.invalidate(key);
        }
    }
 
    public void loginFailed(String key) {
        int attempts = 0;
        try {
            attempts = attemptsCache.get(key);
        } catch (ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }
 
    public boolean isBlocked(String key) {
        try {
            return attemptsCache.get(key) >= MAX_INVALID_LOGIN_ATTEMPTS;
        } catch (ExecutionException e) {
            return false;
        }
    }
    
    public int getBlockTimeInMinutes() {
    	return BLOCK_TIME_IN_MINUTES;
    }

}