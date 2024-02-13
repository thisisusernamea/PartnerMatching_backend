package com.atyupi.partner_matching.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atyupi.partner_matching.model.domain.UserTeam;
import com.atyupi.partner_matching.service.UserTeamService;
import com.atyupi.partner_matching.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 常俊杰
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-02-13 15:10:42
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




