package com.atyupi.partner_matching.service;

import com.atyupi.partner_matching.model.domain.Team;
import com.atyupi.partner_matching.model.domain.User;
import com.atyupi.partner_matching.model.dto.TeamQuery;
import com.atyupi.partner_matching.model.request.TeamJoinRequest;
import com.atyupi.partner_matching.model.request.TeamUpdateRequest;
import com.atyupi.partner_matching.model.vo.TeamUserVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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

    /**
     * 搜索队伍
     * @param teamQuery
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery,boolean isAdmin);

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser);

    /**
     * 用户加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    Boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser);
}
