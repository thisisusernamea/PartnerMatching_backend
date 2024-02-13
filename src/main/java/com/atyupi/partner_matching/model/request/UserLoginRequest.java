package com.atyupi.partner_matching.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 5544977521392906894L;
    private String userAccount;
    private String userPassword;
}
