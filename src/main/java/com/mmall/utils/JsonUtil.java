package com.mmall.utils;

import com.mmall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rabbit on 2019/1/4.
 */
@Slf4j
public class JsonUtil {
    private static ObjectMapper objectMapper;
    static{
        objectMapper = new ObjectMapper();
        //序列化对象的全部属性
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);
        //规定不把日期属性序列化成时间戳格式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        //对空对象不进行序列化，防止出错
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        //规定日期格式的属性转化成指定格式的日期字符串
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANTARD_FORMAT));
        //反序列化的时候若JavaBean中不存在对应的属性，则不进行反序列化，防止出错
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    public static <T> String obj2String(T obj){
        if(obj == null) return null;
        try {
            return obj instanceof String ?  (String)obj : objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.error("object serializable to json string error.", e);
            return null;
        }
    }

    public static <T> String obj2StringPretty(T obj){
        if(obj == null) return null;
        try {
            return obj instanceof String ?  (String)obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            log.error("object serializable to json string error.", e);
            return null;
        }
    }

    public static <T> T string2Obj(String objStr, Class<T> clazz){
        if(StringUtils.isBlank(objStr) || clazz == null){
            return null;
        }
       try {
           return clazz.equals(String.class) ? (T)objStr : objectMapper.readValue(objStr, clazz);
       } catch (IOException e) {
           log.error("json string deserializable to object error.", e);
           return null;
       }
    }

    /**
     * 将复杂类型的对象进行正确反序列化
     * @param objStr
     * @param typeReference 装载着复杂类型的类型引用对象参数
     * @param <T>
     * @return
     */
    public static <T> T string2Obj(String objStr, TypeReference<T> typeReference){
        if(StringUtils.isBlank(objStr) || typeReference == null){
            return null;
        }
        try {
            return typeReference.getType().equals(String.class) ? (T)objStr : (T)objectMapper.readValue(objStr, typeReference);
        } catch (IOException e) {
            log.error("json string deserializable to object error.", e);
            return null;
        }
    }

    /**
     * 将那些装载有特定类型的集合类型对象进行正确地反序列化
     * @param objStr
     * @param collectionClass  集合类型
     * @param elementClass  集合中装载的元素的类型
     * @param <T>
     * @return
     */
    public static <T> T string2Obj(String objStr, Class<?> collectionClass, Class<?>... elementClass){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClass);
        try {
            return objectMapper.readValue(objStr, javaType);
        } catch (IOException e) {
            log.error("json string deserializable to object error.", e);
            return null;
        }
    }



    public static void main(String[] args) {
        User user = new User();
        user.setId(1);
        user.setUsername("Berio");
        user.setCreateTime(new Date());
        String objStr = obj2StringPretty(user);
        log.info(objStr);
    }
}
