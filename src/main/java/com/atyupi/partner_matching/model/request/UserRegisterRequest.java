package com.atyupi.partner_matching.model.request;


import lombok.Data;

import java.io.Serializable;

/**
 * 将用户注册请求中json请求参数转换为java对象
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 6976434215179762316L;
    String userAccount;
    String userPassword;
    String checkPassword;
    String planetCode;
}
