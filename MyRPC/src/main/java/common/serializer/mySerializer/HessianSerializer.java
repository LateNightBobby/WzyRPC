package common.serializer.mySerializer;

public class HessianSerializer implements Serializer{
    @Override
    public byte[] serialize(Object obj) {
        return new byte[0];
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        return null;
    }

    @Override
    public int getType() {
        return 4;
    }
}
