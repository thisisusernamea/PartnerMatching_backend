package com.atyupi.partner_matching.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {
    int code;
    T data;
    String message;
    String description;

    /**
     * 构造方法
     * @param code
     * @param data
     * @param message
     */
    public BaseResponse(int code, T data, String message,String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }
    public BaseResponse(int code, T data,String message) {
        this(code,data,message,"");
    }
    public BaseResponse(int code,T data){
        this(code,data,"","");
    }
    public BaseResponse(int code,String message,String description) {
        this(code,null,message,description);
    }
    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),null,errorCode.getMessage(),errorCode.getDescription());
    }
}
