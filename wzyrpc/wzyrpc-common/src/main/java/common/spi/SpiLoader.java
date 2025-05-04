package common.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import common.serializer.mySerializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpiLoader {
    // 存储已加载的 SPI 实现类的映射
    private static final Map<String, Map<String, Class<? extends Serializer>>> loadedSpiMap = new ConcurrentHashMap<>();

    // 缓存实例，避免重复实例化
    private static final Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    // SPI 配置文件的路径
    private static final String SPI_CONFIG_DIR = "META-INF/serializer/";

    public static void loadSpi(Class<?> serviceInterface) {
        String interfaceName = serviceInterface.getName();

        if (loadedSpiMap.containsKey(interfaceName)) {
            return;
        }

        Map<String, Class<? extends Serializer>> keyClassMap = new HashMap<>();

        List<URL> resources = ResourceUtil.getResources(SPI_CONFIG_DIR + serviceInterface.getName());
        for (URL resource: resources) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.openStream())) ) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && !line.startsWith("#")) {
                        String[] parts = line.split("=");
                        if (parts.length == 2) {
                            String key = parts[0].trim();
                            String className = parts[1].trim();
                            Class<?> implClass = Class.forName(className);
                            if (serviceInterface.isAssignableFrom(implClass)) {
                                keyClassMap.put(key, (Class<? extends Serializer>) implClass);
                            }
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        loadedSpiMap.put(interfaceName, keyClassMap);
    }

    public static <T> T getInstance(Class<T> serviceInterface, String key) {
        String interfaceName = serviceInterface.getName();
        Map<String, Class<? extends Serializer>> keyClassMap = loadedSpiMap.get(interfaceName);

        if (keyClassMap == null) {
            throw new RuntimeException("SPI not loaded for " + interfaceName);
        }

        Class<? extends Serializer> implClass = keyClassMap.get(key);
        if (implClass == null) {
            throw new RuntimeException("No SPI implementation found for key " + key);
        }

        // 从缓存中获取实例，如果不存在则创建
        String implClassName = implClass.getName();
        if (!instanceCache.containsKey(implClassName)) {
            try {
                instanceCache.put(implClassName, implClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Failed to instantiate SPI implementation: " + implClassName, e);
            }
        }

        return (T) instanceCache.get(implClassName);
    }
}
