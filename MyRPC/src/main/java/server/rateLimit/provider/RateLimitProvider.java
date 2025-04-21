package server.rateLimit.provider;

import server.rateLimit.RateLimit;
import server.rateLimit.impl.TokenBucketRateLimit;

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
