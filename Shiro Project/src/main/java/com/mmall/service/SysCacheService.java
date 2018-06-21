package com.mmall.service;

import com.google.common.base.Joiner;
import com.mmall.beans.CacheKeyConstants;
import com.mmall.controller.TestController;
import com.mmall.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.Resource;

@Service
@Slf4j
public class SysCacheService {

    private static final Logger logger= LoggerFactory.getLogger(TestController.class);

    @Resource(name = "redisPool")
    private RedisPool redisPool;

    public void saveCache(String toSaveValue, int timeoutSeconds, CacheKeyConstants prefix){
        saveCache(toSaveValue,timeoutSeconds,prefix,null);
    }

    public void saveCache(String toSaveValue, int timeoutSeconds, CacheKeyConstants prefix,String... keys){
        if (toSaveValue==null){
            return;
        }
        ShardedJedis shardedJedis=null;
        try {
            String cacheKey=generateCacheKey(prefix,keys);
            shardedJedis=redisPool.instance();
            shardedJedis.setex(cacheKey,timeoutSeconds,toSaveValue);
        }catch (Exception e){
            logger.error("save cache exception,prefix:{},key:{}",prefix.name(), JsonMapper.object2String(keys));
        }finally {
            redisPool.safeClose(shardedJedis);
        }
    }

    public String getFromCache(CacheKeyConstants prefix,String... keys){
        ShardedJedis shardedJedis=null;
        String cacheKey=generateCacheKey(prefix,keys);
        try {
            shardedJedis=redisPool.instance();
            String value=shardedJedis.get(cacheKey);
            return value;
        }catch (Exception e){
            logger.error("get from cache exception,prefix:{},keys:{}",prefix.name(), JsonMapper.object2String(keys));
            return null;
        }finally {
            redisPool.safeClose(shardedJedis);
        }

    }

    private String generateCacheKey(CacheKeyConstants prefix,String... keys){
        String key=prefix.name();
        if (keys!=null&&keys.length>0){
            key+="_"+ Joiner.on("_").join(keys);
        }
        return key;
    }
}
