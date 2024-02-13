package com.atyupi.partner_matching.service;

import com.atyupi.partner_matching.model.domain.Team;
import com.atyupi.partner_matching.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 常俊杰
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-02-13 15:09:03
*/
public interface TeamService extends IService<Team> {
    /**
     * 创建队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);
}
