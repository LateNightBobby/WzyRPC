package common.serializer.mySerializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import common.message.RpcRequest;
import common.message.RpcResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer implements Serializer{

    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        kryo.register(Object[].class);
        kryo.register(Class[].class);
        kryo.register(Class.class);

        kryo.setReferences(true);
        return kryo;
    });
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            // Object->byte:将对象序列化为byte数组
            kryo.writeObject(output, obj);
            output.flush();
            return output.toBytes();
        } catch (Exception e) {
            e.printStackTrace();
//            log.error("Serialization failed", e);
            throw new RuntimeException();
        }
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        try (
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                Input input = new Input(byteArrayInputStream)
        ) {
            Kryo kryo = kryoThreadLocal.get();
            Class<?> targetClass = (messageType == 0) ? RpcRequest.class : RpcResponse.class;
            return kryo.readObject(input, targetClass);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Kryo 反序列化失败", e);
        }
    }

    @Override
    public int getType() {
        return 2;
    }
}
