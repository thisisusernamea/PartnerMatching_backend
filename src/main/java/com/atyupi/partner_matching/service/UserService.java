package com.atyupi.partner_matching.service;

import com.atyupi.partner_matching.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 常俊杰
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-09-04 08:25:26
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 新用户Id
     */
    long userRegister(String userAccount,String userPassword,String checkPassword,String planetCode);

    /**
     * 用户登录
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 用户脱敏信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户注销
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser 数据库中的用户记录
     * @return 用户脱敏记录
     */
    User getSafetyUser(User originUser);

    /**
     * 根据标签搜索用户
     * @param tagNameList 用户拥有的标签列表
     * @return
     */
    List<User> searchUserByTags(List<String> tagNameList);

    /**
     * 鉴权 -> 获取当前登录用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 修改用户信息
     * @param user
     * @param loginUser
     * @return
     */
    int updateUser(User user,User loginUser);

    /**
     * 鉴权 -> 是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);
    boolean isAdmin(User loginUser);
}
