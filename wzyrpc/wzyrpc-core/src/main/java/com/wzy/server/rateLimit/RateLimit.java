package com.wzy.server.rateLimit;

public interface RateLimit {
    //尝试获取访问许可
    boolean getToken();

    void shutdown();
}
