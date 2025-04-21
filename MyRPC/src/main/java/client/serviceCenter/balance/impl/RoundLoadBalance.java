package client.serviceCenter.balance.impl;

import client.serviceCenter.balance.LoadBalance;

import java.util.List;

public class RoundLoadBalance implements LoadBalance {
    private int choose = -1;
    @Override
    public String balance(List<String> addressList) {
        choose++;
        choose %= addressList.size();
        System.out.println("轮询负载均衡选择"+choose+"号服务器"+addressList.get(choose));
        return addressList.get(choose);
    }

    @Override
    public void addNode(String node) {

    }

    @Override
    public void delNode(String node) {

    }
}
