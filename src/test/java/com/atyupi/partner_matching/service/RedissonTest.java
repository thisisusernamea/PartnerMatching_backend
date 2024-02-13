package com.atyupi.partner_matching.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RedissonTest {
    @Resource
    RedissonClient redissonClient;

    @Test
    public void test(){
        //list test-list相当于key
        RList<String> rList = redissonClient.getList("test-list");
        rList.add("yupi");
        System.out.println("rList:" + rList.get(0));
        //map
        RMap<String, Object> map = redissonClient.getMap("test-map");
        map.put("yupi",1);
        System.out.println(" map.get(yupi)" +  map.get("yupi"));
    }
}
