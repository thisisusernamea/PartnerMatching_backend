package com.atyupi.partner_matching.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出队伍请求体
 */
@Data
public class TeamQuitRequest implements Serializable {

    private static final long serialVersionUID = 2096549429558710412L;

    /**
     * 队伍id
     */
    private Long teamId;

}
