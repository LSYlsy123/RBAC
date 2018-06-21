package com.mmall.util;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 把类转化为json对象
 * 把json字符串转化为类对象
 */
@Slf4j
public class JsonMapper {

    private static ObjectMapper objectMapper=new ObjectMapper();
    private static final Logger logger= LoggerFactory.getLogger(JsonMapper.class);

    //排除为空的字段
    static {
        //config
        objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        objectMapper.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
    }

    //把对象转化为字符串
    public static <T> String object2String(T src){
        if (src==null){
            return null;
        }
        try {
            //如果是string，直接强转，如果不是调用方法
            return src instanceof String ? (String)src :objectMapper.writeValueAsString(src);
        }catch (Exception e){
            //如果转化不成功报错
            logger.warn("parse object to String exception , error{}",e);
            return null;
        }
    }

    //把字符串转化为对象
    public static <T> T string2Object(String src, TypeReference<T> tTypeReference){

        if (src==null||tTypeReference==null){
            return null;
        }

        try {
            return (T)(tTypeReference.getType().equals(String.class) ? src : objectMapper.readValue(src,tTypeReference));
        }catch (Exception e){
            logger.warn("parse String to object exception , String:{} , TypeReference<T> , error:{}",src,tTypeReference.getType(),e);
            return null;
        }

    }
}
