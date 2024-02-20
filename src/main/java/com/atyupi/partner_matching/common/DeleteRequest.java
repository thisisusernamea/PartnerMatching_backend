package com.atyupi.partner_matching.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = -2779552937880524231L;

    /**
     * id
     */
    private Long id;
}
