package com.mmall.util;

import com.google.common.base.Splitter;

import java.util.List;
import java.util.stream.Collectors;

public class StringUtil {

    //1,2,3,4,,=>{1,2,3,4}
    public static List<Integer> splitToListInt(String str){
        //以"，"分隔，去掉空字符串转换成string数组
        List<String> strList= Splitter.on(",").trimResults().omitEmptyStrings().splitToList(str);
        List<Integer> list=strList.stream().map(strItem -> Integer.parseInt(strItem)).collect(Collectors.toList());
        return list;
    }
}
