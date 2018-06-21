package com.mmall.util;

import org.apache.commons.lang3.StringUtils;

public class LevelUtil {

    //各层级之间的分隔符
    public final static String SEPARATOR=".";

    //root的id
    public final static String ROOT="0";

    //部门level计算规则
    //0.1
    //0.1.2
    //0.1.3
    //0.2
    public static String calculateLevel(String parenLevel,int parenId){

        if (StringUtils.isBlank(parenLevel)){
            return ROOT;
        }else {
            return StringUtils.join(parenLevel,SEPARATOR,parenId);
        }


    }

}
