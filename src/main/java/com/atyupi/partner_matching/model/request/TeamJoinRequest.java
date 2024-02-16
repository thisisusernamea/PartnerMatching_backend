package com.atyupi.partner_matching.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamJoinRequest implements Serializable {

    private static final long serialVersionUID = 2096549429558710412L;

    /**
     * 队伍id
     */
    private Long id;

    /**
     * 密码
     */
    private String password;
}
