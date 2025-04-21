package client.retry;

import client.rpcClient.RpcClient;
import com.github.rholder.retry.*;
import common.message.RpcRequest;
import common.message.RpcResponse;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class GuavaRetry {
    private RpcClient rpcClient;

    public RpcResponse sendServiceWithRetry(RpcRequest request, RpcClient client) {
        this.rpcClient = client;
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                //异常重试
                .retryIfException()
                //返回结果为error重试
                .retryIfResult(response -> Objects.equals(response.getCode(), 500))
                //重试等待策略：等待2s后重试
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
                //重试停止策略：超过3次
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        System.out.println("RetryListener 第" + attempt.getAttemptNumber() + "次请求");
                    }
                })
                .build();
        try {
            return retryer.call(() -> rpcClient.sendRequest(request));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RpcResponse.fail();
    }
}
