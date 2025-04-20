package client.rpcClient;

import common.message.RpcRequest;
import common.message.RpcResponse;

public interface RpcClient {
    public RpcResponse sendRequest(RpcRequest request);
}
