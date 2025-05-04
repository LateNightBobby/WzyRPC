package com.wzy.server.rateLimit.provider;

import com.wzy.server.rateLimit.RateLimit;
import com.wzy.server.rateLimit.impl.TokenBucketRateLimit;

import java.util.HashMap;
import java.util.Map;

public class RateLimitProvider {
    private Map<String, RateLimit> rateLimitMap = new HashMap<>();

    public RateLimit getRateLimit(String interfaceName) {
        if (!rateLimitMap.containsKey(interfaceName)) {
            RateLimit rateLimit=new TokenBucketRateLimit(100,10);
            rateLimitMap.put(interfaceName,rateLimit);
            return rateLimit;
        }
        return rateLimitMap.get(interfaceName);
    }
}
