package com.atyupi.partner_matching.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 星球用户信息excel
 */
@Data
@EqualsAndHashCode
public class XingQiuTabUserInfo {
    /**
     * 用名字去匹配，这里需要注意，如果名字重复，会导致只有一个字段读取到数据
     */
    @ExcelProperty("成员编号")
    private String plantCode;
    @ExcelProperty("成员昵称")
    private String userName;
}