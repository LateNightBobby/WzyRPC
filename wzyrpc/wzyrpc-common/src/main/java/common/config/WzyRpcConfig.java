package common.config;

import com.wzy.client.serviceCenter.balance.impl.ConsistencyHashLoadBalance;
import com.wzy.server.serviceRegister.impl.ZKServiceRegister;
import common.serializer.mySerializer.Serializer;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class WzyRpcConfig {
    private String name = "wzyrpc";

    private Integer port = 9999;

    private String host = "localhost";

    private String version = "1.0.0";

    private String registry = new ZKServiceRegister().toString();

    private String serializer = Serializer.getSerializerByCode(1).toString();

    private String loadBalance = new ConsistencyHashLoadBalance().toString();
}
