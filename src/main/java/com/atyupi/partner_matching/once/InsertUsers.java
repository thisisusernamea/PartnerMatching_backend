package com.atyupi.partner_matching.once;

import com.atyupi.partner_matching.mapper.UserMapper;
import com.atyupi.partner_matching.model.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

@Component
public class InsertUsers {
    @Resource
    private UserMapper userMapper;

    /**
     * 批量插入用户数据
     */
    /*项目启动5s后执行该任务,每Long.MAX_VALUE的时间间隔执行一次该任务*/
    //@Scheduled(initialDelay = 5000,fixedRate = Long.MAX_VALUE)
    public void doInserts(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100;
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
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
