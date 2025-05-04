package com.wzy.server.rateLimit.impl;

import com.wzy.server.rateLimit.RateLimit;

import java.util.concurrent.atomic.AtomicInteger;

public class TokenBucketRateLimit implements RateLimit {
    private static int CAPACITY;
    private static int RATE;
    private volatile AtomicInteger curCapacity;
    private volatile long timeStamp = System.currentTimeMillis();

//    private final ScheduledExecutorService scheduler;
    public TokenBucketRateLimit(int rate, int capacity) {
        RATE = rate;
        CAPACITY = capacity;
        curCapacity = new AtomicInteger(CAPACITY);

//        scheduler = Executors.newScheduledThreadPool(1);
//        scheduler.scheduleAtFixedRate(() -> {
//            synchronized (this) {
//                if (curCapacity.get() < CAPACITY) {
//                    curCapacity.incrementAndGet();
//                }
//            }
//        }, 0, 1000 / RATE, TimeUnit.MILLISECONDS);
    }
    @Override
    public synchronized boolean getToken() {
        if (curCapacity.get() > 0) {
            curCapacity.decrementAndGet();
            return true;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - timeStamp >= RATE) {
            int refillNum = (int) (((currentTime - timeStamp) / RATE) - 1);
            curCapacity.set(Math.min(curCapacity.get() + refillNum, CAPACITY));
            timeStamp = currentTime;
            return true;
        }
        return false;
    }

    @Override
    public void shutdown() {
//        scheduler.shutdown();
    }
}
