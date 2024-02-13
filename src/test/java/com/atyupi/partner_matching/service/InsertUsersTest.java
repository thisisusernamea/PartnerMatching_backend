package com.atyupi.partner_matching.service;

import com.atyupi.partner_matching.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootTest
public class InsertUsersTest {
    @Resource
    UserService userService;

    @Test
    public void doInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假用户");
            user.setUserAccount("fakeUser");
            user.setUserPassword("12345678");
            user.setGender(0);
            user.setPhone("123");
            user.setEmail("123@qq.com");
            user.setUserStatus(0);
            user.setAvatarUrl("https://q4.qlogo.cn/g?b=qq&nk=2742895138@qq.com&s=3?d=retro");
            user.setUserRole(0);
            user.setPlanetCode("11111111");
            user.setTags("[]");
            userList.add(user);
        }
        userService.saveBatch(userList,1000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    @Test
    //将10w条数据分成10组,开启10个线程同时执行,每个线程插入1w条数据
    public void doConcurrencyInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int BATCH_SIZE = 10000;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < 10; i++) {
            ArrayList<User> userList = new ArrayList<>();
            while (true){
                j++;
                User user = new User();
                user.setUsername("假用户");
                user.setUserAccount("fakeUser");
                user.setUserPassword("12345678");
                user.setGender(0);
                user.setPhone("123");
                user.setEmail("123@qq.com");
                user.setUserStatus(0);
                user.setAvatarUrl("https://q4.qlogo.cn/g?b=qq&nk=2742895138@qq.com&s=3?d=retro");
                user.setUserRole(0);
                user.setPlanetCode("11111111");
                user.setTags("[]");
                userList.add(user);
                if(j % BATCH_SIZE == 0){
                    break;
                }
            }
            //创建10个异步任务
            CompletableFuture<Void> futureTask = CompletableFuture.runAsync(() ->{
                System.out.println("threadName:" + Thread.currentThread().getName());
                userService.saveBatch(userList,BATCH_SIZE);
            });
            futureList.add(futureTask);
        }
        //使用.join()实现阻塞 -> 10个异步任务都执行结束,才会执行下一条命令
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
