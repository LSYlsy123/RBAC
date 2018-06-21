package com.mmall.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.exception.ParamException;
import org.apache.commons.collections.MapUtils;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

public class BeanValidator {

    //全局校验工厂
    private static ValidatorFactory validatorFactory= Validation.buildDefaultValidatorFactory();

    //单个类的校验
    public static <T>Map<String,String> validate(T t,Class... groups){
        Validator validator=validatorFactory.getValidator();
        //获取校验结果
        Set validateResult=validator.validate(t,groups);
        //如果没有值
        if (validateResult.isEmpty()){
            return Collections.emptyMap();
            //如果有错误结果，进行遍历
        }else {
            LinkedHashMap errors= Maps.newLinkedHashMap();
            Iterator iterator=validateResult.iterator();
            while (iterator.hasNext()){
                ConstraintViolation violation=(ConstraintViolation)iterator.next();
                //将遍历出来的错误放入errors，有问题字段+错误信息
                errors.put(violation.getPropertyPath().toString(),violation.getMessage());
            }
            return errors;
        }
    }

    //list类校验方法
    public static Map<String,String> validateList(Collection<?> collection){
        //判断collection是否为空
        Preconditions.checkNotNull(collection);
        Iterator iterator=collection.iterator();
        Map errors;
        do {
            //如果是空值，返回空的map
            if (!iterator.hasNext()){
                return Collections.emptyMap();
            }
            Object object=iterator.next();
            errors=validate(object,new Class[0]);

        }while (errors.isEmpty());
        return errors;
    }

    //传什么都行的校验方法
    public static Map<String,String> validateObject(Object first,Object... objects){
        //是list的时候
        if (objects!=null&&objects.length>0){
            return validateList(Lists.asList(first,objects));
            //单个类的时候
        }else {
           return validate(first,new Class[0]);
        }
    }

    //判断是否有异常
    public static void check(Object param) throws ParamException{
        Map<String,String> map=BeanValidator.validateObject(param);
        //有错误信息抛异常
//        if (map!=null&&map.entrySet().size()>0){
        if (MapUtils.isNotEmpty(map)){
            throw new ParamException(map.toString());
        }
    }

}
