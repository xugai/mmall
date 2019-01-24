package com.mmall.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by rabbit on 2018/2/6.
 */
@Slf4j
public class PropertiesUtil {
//    private static Logger log = LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties props;

    static {
        String fileName = "mmall.properties";
        props = new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
        } catch (IOException e) {
            log.error("配置文件读取异常",e);
        }
    }

    public static String getProperty(String key){
        String value = props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            return null;
        }
        return value.trim();
    }

    public static String getProperty(String key,String defaultValue){

        String value = props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            value = defaultValue;
        }
        return value.trim();
    }

    public static int getPropertyInInteger(String key){
        String value = props.getProperty(key);
        if(StringUtils.isBlank(value)){
            return 0;
        }
        return Integer.parseInt(value.trim());
    }

    public static int getPropertyInInteger(String key, int defaultValue){
        String value = props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            return defaultValue == 0 ? 0 : defaultValue;
        }
        return Integer.parseInt(value.trim());
    }

    public static boolean getPropertyInBoolean(String key){
        String value = props.getProperty(key);
        if(StringUtils.isBlank(value)){
            return true;
        }
        return Boolean.parseBoolean(value.trim());
    }

    public static boolean getPropertyInBoolean(String key, boolean defaultValue){
        String value = props.getProperty(key);
        if(StringUtils.isBlank(value)){
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }
}
