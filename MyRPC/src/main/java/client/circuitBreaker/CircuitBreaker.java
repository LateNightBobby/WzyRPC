package client.circuitBreaker;

import java.util.concurrent.atomic.AtomicInteger;

public class CircuitBreaker {
    private CircuitBreakerState state = CircuitBreakerState.CLOSED;
    private AtomicInteger successCount = new AtomicInteger(0);
    private AtomicInteger failCount = new AtomicInteger(0);
    private AtomicInteger requestCount = new AtomicInteger(0);

    private final int failureThreshold;
    private final double halfOpenSuccessRate;
    private final long resetTimePeriod;
    private long lastFailureTime = 0;

    public CircuitBreaker(int failureThreshold, double halfOpenSuccessRate, long resetTimePeriod) {
        this.failureThreshold = failureThreshold;
        this.halfOpenSuccessRate = halfOpenSuccessRate;
        this.resetTimePeriod = resetTimePeriod;
    }

    public synchronized boolean allowRequest() {
        long currentTime = System.currentTimeMillis();
        switch (state) {
            case OPEN:
                if (currentTime - lastFailureTime > resetTimePeriod) {
                    //达到等待时间 回到半开状态进行尝试
                    state = CircuitBreakerState.HALF_OPEN;
                    resetCounts();
                    return true;
                }
                return false;
            case HALF_OPEN:
                requestCount.incrementAndGet();
                return true;
            case CLOSED:
            default:
                return true;
        }
    }

    public synchronized void recordSuccess() {
        if (state == CircuitBreakerState.CLOSED) {
            resetCounts();
            return;
        }
        if (state == CircuitBreakerState.HALF_OPEN) {
            int sucCnt = successCount.incrementAndGet();
            if (sucCnt >= halfOpenSuccessRate * requestCount.get()) {
                state = CircuitBreakerState.CLOSED;
                resetCounts();;
            }
        }
    }

    public synchronized void recordFailure() {
        int failCnt = failCount.incrementAndGet();
        lastFailureTime = System.currentTimeMillis();
        if (state == CircuitBreakerState.HALF_OPEN) {
            state = CircuitBreakerState.OPEN;
        } else if (failCnt >= failureThreshold) {
            state = CircuitBreakerState.OPEN;
        }
    }

    private void resetCounts() {
        failCount.set(0);
        successCount.set(0);
        requestCount.set(0);
    }
    public CircuitBreakerState getState() {
        return state;
    }
}

enum CircuitBreakerState {
    //关闭，开启，半开
    CLOSED, OPEN, HALF_OPEN
}
