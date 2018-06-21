package com.mmall.controller;


import com.mmall.common.ApplicationContextHelper;
import com.mmall.common.JsonData;
import com.mmall.dao.SysAclModuleMapper;
import com.mmall.exception.ParamException;

import com.mmall.model.SysAclModule;
import com.mmall.param.TestVo;
import com.mmall.util.BeanValidator;
import com.mmall.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/test")
public class TestController {
    private static final Logger logger= LoggerFactory.getLogger(TestController.class);

    @RequestMapping("/hello.json")
    @ResponseBody
    public JsonData hello(){
        logger.info("hello");
        throw new RuntimeException("test exception");
//        return JsonData.success("hello,permission!");
    }

    @ResponseBody
    @RequestMapping("/validate.json")
    public JsonData validate(TestVo vo)throws ParamException {
        logger.info("validate");
        SysAclModuleMapper moduleMapper= ApplicationContextHelper.popBean(SysAclModuleMapper.class);
        SysAclModule module=moduleMapper.selectByPrimaryKey(1);
        logger.info(JsonMapper.object2String(module));
        BeanValidator.check(vo);
        return JsonData.success("test validate");
    }
}
