package com.mmall.service;
import com.mmall.controller.TestController;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import javax.annotation.Resource;

@Service("redisPool")
@Slf4j
public class RedisPool {

    private static final Logger logger= LoggerFactory.getLogger(TestController.class);

    @Resource(name = "shardedJedisPool")
    private ShardedJedisPool shardedJedisPool;

    public ShardedJedis instance(){
        return shardedJedisPool.getResource();
    }

    public void safeClose(ShardedJedis shardedJedis){
        try {
            if (shardedJedis!=null){
                shardedJedis.close();
            }
        }catch (Exception e){
            logger.error("return redis resource exception",e);
        }
    }

}
