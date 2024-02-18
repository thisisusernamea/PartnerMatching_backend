package com.atyupi.partner_matching.service.impl;

import com.atyupi.partner_matching.common.ErrorCode;
import com.atyupi.partner_matching.exception.BusinessException;
import com.atyupi.partner_matching.model.domain.User;
import com.atyupi.partner_matching.model.domain.UserTeam;
import com.atyupi.partner_matching.model.dto.TeamQuery;
import com.atyupi.partner_matching.model.enums.TeamStatusEnum;
import com.atyupi.partner_matching.model.request.TeamJoinRequest;
import com.atyupi.partner_matching.model.request.TeamQuitRequest;
import com.atyupi.partner_matching.model.request.TeamUpdateRequest;
import com.atyupi.partner_matching.model.vo.TeamUserVO;
import com.atyupi.partner_matching.model.vo.UserVO;
import com.atyupi.partner_matching.service.UserService;
import com.atyupi.partner_matching.service.UserTeamService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atyupi.partner_matching.model.domain.Team;
import com.atyupi.partner_matching.service.TeamService;
import com.atyupi.partner_matching.mapper.TeamMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
* @author 常俊杰
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-02-13 15:09:03
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    UserTeamService userTeamService;

    @Resource
    UserService userService;

    @Override
    //开启事务 rollbackFor -> 事务回滚时抛出的异常
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        //请求参数为空
        if(team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //是否登录，未登录不允许创建
        if (loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //  队伍人数 > 1 且 <= 20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不满足要求");
        }
        //  队伍标题 <= 20
        String name = team.getName();
        if(StringUtils.isBlank(name) || name.length() > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍标题不满足要求");
        }
        //  描述 <= 512
        String description = team.getDescription();
            //队伍描述可以为空
        if(StringUtils.isNotBlank(description) && description.length() > 512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述过长");
        }
        //  是否公开（int）不传默认为0，即公开
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if(statusEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不满足要求");
        }
        //  如果 status=2 是加密状态，一定要有密码，且密码 <= 32
        String password = team.getPassword();
        if(TeamStatusEnum.SECRET.equals(statusEnum) && (StringUtils.isBlank(password) || password.length() > 32)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码设置不正确");
        }
        //超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if(new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"超时时间应大于当前时间");
        }
        //校验用户最多创建 5 个队伍
        //todo 有bug,用户在某一瞬间疯狂点击创建队伍,可能创建 100 个队伍
        final long userId = loginUser.getId();
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        long hasTeamNum = this.count(queryWrapper);
        if(hasTeamNum >= 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户最多创建 5 个队伍");
        }
        //插入 队伍信息 到 队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();
        if(!result || teamId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }
        //插入 用户队伍信息 到 用户_队伍表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if(!result){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }
        return teamId;
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery,boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        //组合查询条件
        if(teamQuery != null){
            //根据队伍id查询
            Long id = teamQuery.getId();
            if(id != null && id > 0){
                queryWrapper.eq("id",id);
            }
            //通过某个关键词同时对名称和描述查询
            String searchText = teamQuery.getSearchText();
            if(StringUtils.isNotBlank(searchText)){
                queryWrapper.and(qw -> qw.like("name",searchText).or().like("description",searchText));
            }
            //根据队伍名称查询
            String name = teamQuery.getName();
            if(StringUtils.isNotBlank(name)){
                queryWrapper.like("name",name);
            }
            String description = teamQuery.getDescription();
            if(StringUtils.isNotBlank(description)){
                queryWrapper.like("description",description);
            }
            //根据队伍人数查询
            Integer maxNum = teamQuery.getMaxNum();
            if(maxNum != null && maxNum > 0){
                queryWrapper.eq("maxNum",maxNum);
            }
            //根据创建人查询
            Long userId = teamQuery.getUserId();
            if(userId != null && userId > 0){
                queryWrapper.eq("userId",userId);
            }
            //根据队伍状态查询(只有管理员才能查看加密还有非公开的房间)
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if(statusEnum == null){
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if(!isAdmin && !statusEnum.equals(TeamStatusEnum.PUBLIC)){
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status",statusEnum.getValue());
        }
        //不展示已过期的队伍
        queryWrapper.and(qw -> qw.isNull("expireTime").or().gt("expireTime",new Date()));
        List<Team> teamList = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(teamList)){
            return new ArrayList<>();
        }
        ArrayList<TeamUserVO> teamUserVOList = new ArrayList<>();
        //关联查询队伍和创建人的信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if(userId == null){
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            org.springframework.beans.BeanUtils.copyProperties(team,teamUserVO);
            //脱敏用户信息
            if(user != null){
                UserVO userVO = new UserVO();
                org.springframework.beans.BeanUtils.copyProperties(user,userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser) {
        //判断请求参数是否为空
        if(teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //查询队伍是否存在
        Long id = teamUpdateRequest.getId();
        if(id == null || id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(id);
        if(oldTeam == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        //只有管理员或者队伍的创建者可以修改
        if(!loginUser.getId().equals(oldTeam.getUserId()) && !userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        //如果队伍状态改为加密,修改队伍信息时必须要有密码
        Integer status = teamUpdateRequest.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if(statusEnum.equals(TeamStatusEnum.SECRET)){
            if(StringUtils.isBlank(teamUpdateRequest.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密房间必须设置密码！");
            }
        }
        //更新
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,updateTeam);
        boolean result = this.updateById(updateTeam);
        return result;
    }

    @Override
    public Boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser) {
        if(teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //队伍必须存在
        Long teamId = teamJoinRequest.getTeamId();
        Team team = getTeamById(teamId);
        //只能加入未过期的队伍
        Date expireTime = team.getExpireTime();
        if(expireTime != null && expireTime.before(new Date())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已过期");
        }
        //不能加入私有队伍
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if(TeamStatusEnum.PRIVATE.equals(teamStatusEnum)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"禁止加入私有队伍");
        }
        //如果加入的队伍是加密的，必须密码匹配才可以
        String password = teamJoinRequest.getPassword();
        if(TeamStatusEnum.SECRET.equals(teamStatusEnum)){
            if(StringUtils.isBlank(password) || !password.equals(team.getPassword()))
                throw new BusinessException(ErrorCode.NULL_ERROR,"密码错误");
        }
        //用户最多加入5个队伍
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId",userId);
        long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
        if(hasJoinNum > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"最多创建和加入5个队伍");
        }
        //只能加入未满的队伍
        long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
        if(teamHasJoinNum >= team.getMaxNum()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已满");
        }
        //不能重复加入已加入的队伍
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId",userId);
        userTeamQueryWrapper.eq("teamId",teamId);
        long hasUserJoinTeam = userTeamService.count(userTeamQueryWrapper);
        if(hasUserJoinTeam > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户已加入该队伍");
        }
        //新增用户_队伍关联信息
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        boolean result = userTeamService.save(userTeam);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //校验队伍是否存在
        Long teamId = teamQuitRequest.getTeamId();
        Team team = getTeamById(teamId);
        //校验当前登录用户是否加入队伍
        Long loginUserId = loginUser.getId();
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setTeamId(teamId);
        queryUserTeam.setUserId(loginUserId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(queryUserTeam);
        long count = userTeamService.count(queryWrapper);
        if (count <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您尚未加入该队伍,无需退出队伍！");
        }
        //退出队伍
        long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
        //队伍只剩一人,直接解散队伍
        if (teamHasJoinNum == 1) {
            //删除队伍
            this.removeById(teamId);
        }
        //队伍还剩至少两人
        else {
            //是否为队长,如果是队长,权限转移给第二早加入该队伍的用户
            if (loginUserId.equals(team.getUserId())) {
                //取 user_team表 中指定teamId 且 id最小的2条数据
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() < 2) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                //更新当前队伍队长
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderId);
                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍队长失败");
                }
            }
        }
        //移除用户_队伍关联关系
        return userTeamService.remove(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long id,User loginUser) {
        //校验队伍是否存在
        Team team = getTeamById(id);
        long teamId = team.getId();
        //校验当前登录用户是不是队伍的队长
        if(!loginUser.getId().equals(team.getUserId())){
            throw new BusinessException(ErrorCode.NO_AUTH,"无访问权限");
        }
        //移除所有加入队伍的关联信息
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
            //若删除队伍关系后,服务器宕机导致队伍信息没有被删除,就会出现脏数据,所以要使用事务
        boolean result = userTeamService.remove(userTeamQueryWrapper);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除队伍关系失败");
        }
        //删除队伍
        return this.removeById(teamId);
    }

    /**
     * 获取指定队伍的当前人数
     * @param teamId
     * @return
     */
    private long countTeamUserByTeamId(long teamId){
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId",teamId);
        return userTeamService.count(userTeamQueryWrapper);
    }

    /**
     * 根据 id 获取队伍信息
     * @param teamId
     * @return
     */
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        return team;
    }
}




