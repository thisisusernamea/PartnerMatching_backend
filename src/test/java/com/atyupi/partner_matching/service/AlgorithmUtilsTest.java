package com.atyupi.partner_matching.service;

import com.atyupi.partner_matching.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * 算法工具类测试
 */
public class AlgorithmUtilsTest {

    @Test
    void testMinDistance(){
        String str1 = "鱼皮是狗";
        String str2 = "鱼皮不是狗";
        String str3 = "鱼皮是鱼不是狗";
        int score1 = AlgorithmUtils.minDistance(str1, str2);
        int score2 = AlgorithmUtils.minDistance(str1, str3);
        System.out.println(score1);//1
        System.out.println(score2);//3
    }

    @Test
    void testCompareTags(){
        List<String> tagList1 = Arrays.asList("Java", "大一", "男");
        List<String> tagList2 = Arrays.asList("Java", "大二", "女");
        List<String> tagList3 = Arrays.asList("Python", "大二", "女");
        int score1 = AlgorithmUtils.minDistance(tagList1, tagList2);
        int score2 = AlgorithmUtils.minDistance(tagList1, tagList3);
        System.out.println(score1);//2
        System.out.println(score2);//3
    }
}
