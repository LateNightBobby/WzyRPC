package com.wzy.client.circuitBreaker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CircuitBreakProvider {
    private Map<String, CircuitBreaker> circuitBreakerMap = new ConcurrentHashMap<>();

    public synchronized CircuitBreaker getCircuitBreaker(String serviceName) {
        CircuitBreaker circuitBreaker;
        if (circuitBreakerMap.containsKey(serviceName)) {
            circuitBreaker = circuitBreakerMap.get(serviceName);
        } else {
            System.out.println("serviceName= " + serviceName + " 创建新的熔断器");
            circuitBreaker = new CircuitBreaker(1, 0.5, 10000);
            circuitBreakerMap.put(serviceName, circuitBreaker);
        }
        return circuitBreaker;
    }
}
