package com.atyupi.partner_matching.job;

import com.atyupi.partner_matching.model.domain.User;
import com.atyupi.partner_matching.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 */
@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    //重点用户
    private List<Long> mainUserList = Arrays.asList(1L);
    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;

    //每天执行,预热推荐用户(秒 分 时 日 月 年)
    @Scheduled(cron = "0 26 14 * * *")
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("PartnerMatching:precachejob:docache:lock");
        try {
            //只有一个线程(服务器)能获取到锁
            if(lock.tryLock(0,-1,TimeUnit.MILLISECONDS)){
                System.out.println("getLock:" + Thread.currentThread().getId());
                //将重点用户的主页推荐数据写入缓存
                for (Long userId:mainUserList) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    String redisKey = String.format("PartnerMatching:user:recommend:%s", userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    try {
                        valueOperations.set(redisKey,userPage,30000,TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error",e);
                    }
                }
            }
        } catch (InterruptedException e) {
           log.error("doCacheRecommendUser error",e);
        }finally {
            //只能自己释放锁
            if(lock.isHeldByCurrentThread()){
                System.out.println("unLock:" + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
}
