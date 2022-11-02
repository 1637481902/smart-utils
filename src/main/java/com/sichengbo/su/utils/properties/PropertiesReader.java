package com.sichengbo.su.utils.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件动态读取
 */
public class PropertiesReader {

    private static final Properties property = new Properties();

    private static final Logger log = LoggerFactory.getLogger(PropertiesReader.class);

    static {
        try {
            log.info("reader source...");
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getSystemResourceAsStream("application.properties");
            property.load(is);
            String source = property.getProperty("spring.profiles.active");

            if ("dev".equals(source) || "test".equals(source) || "prod".equals(source)) {
                log.info("reader source success...");
            } else {
                log.error("please check application.properties ---> spring.profiles.active");
            }

            InputStream ist = Thread.currentThread().getContextClassLoader()
                    .getSystemResourceAsStream("application-" + source + ".properties");
            property.load(ist);
        } catch (Exception e) {
        }
    }

    /**
     * 获取文字
     *
     * @param key
     * @return String
     */
    public static String get(String key) {
        return property.getProperty(key);
    }

    /**
     * 获取整数
     *
     * @param key
     * @return Integer
     */
    public static Integer getInteger(String key) {
        String value = get(key);
        return null == value ? null : Integer.valueOf(value);
    }

    /**
     * 获取布尔
     *
     * @param key
     * @return Boolean
     */
    public static Boolean getBoolean(String key) {
        String value = get(key);
        return null == value ? null : Boolean.valueOf(value);
    }
}
