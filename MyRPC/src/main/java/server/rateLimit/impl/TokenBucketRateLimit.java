package server.rateLimit.impl;

import server.rateLimit.RateLimit;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TokenBucketRateLimit implements RateLimit {
    private static int CAPACITY;
    private static int RATE;
    private volatile AtomicInteger curCapacity;
    private volatile long timeStamp = System.currentTimeMillis();

    private final ScheduledExecutorService scheduler;
    public TokenBucketRateLimit(int rate, int capacity) {
        RATE = rate;
        CAPACITY = capacity;
        curCapacity = new AtomicInteger(CAPACITY);

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            synchronized (this) {
                if (curCapacity.get() < CAPACITY) {
                    curCapacity.incrementAndGet();
                }
            }
        }, 0, 1000 / RATE, TimeUnit.MILLISECONDS);
    }
    @Override
    public synchronized boolean getToken() {
        if (curCapacity.get() > 0) {
            curCapacity.decrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public void shutdown() {
        scheduler.shutdown();
    }
}
