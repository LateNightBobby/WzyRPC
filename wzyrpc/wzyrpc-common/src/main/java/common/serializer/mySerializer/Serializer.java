package common.serializer.mySerializer;

public interface Serializer {
    // 对象序列化为字节数组
    byte[] serialize(Object obj);

    //根据指定的消息格式转换回对象
    Object deserialize(byte[] bytes, int messageType);

    //返回使用的序列化器 -0：jdk， 1： json
    int getType();

    static Serializer getSerializerByCode(int code) {
        switch (code) {
            case 0:
                return new ObjectSerializer();
            case 1:
                return new JsonSerializer();
            case 2:
                return new KryoSerializer();
            case 3:
                return new ProtostuffSerializer();
            case 4:
                return new HessianSerializer();
            default:
                return null;
        }
    }
}
