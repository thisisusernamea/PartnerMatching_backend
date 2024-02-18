package com.atyupi.partner_matching.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamAddRequest implements Serializable {

    private static final long serialVersionUID = 2096549429558710412L;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 状态 0-公开 1-私有 2-加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;
}
