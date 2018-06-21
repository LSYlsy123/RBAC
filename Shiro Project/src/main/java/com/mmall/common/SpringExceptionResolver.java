package com.mmall.common;

import com.mmall.exception.ParamException;
import com.mmall.exception.PermissionException;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {
    private static final Logger logger= LoggerFactory.getLogger(SpringExceptionResolver.class);
    @Override
    public ModelAndView resolveException(HttpServletRequest Request, HttpServletResponse hResponse, Object o, Exception e) {
        String url=Request.getRequestURL().toString();
        ModelAndView MV;
        String defaultMsg="System Error";
        //.json,.page
        //这里要求项目中所有请求json数据，都使用.json结尾
        if (url.endsWith(".json")){
            if (e instanceof PermissionException||e instanceof ParamException){
                JsonData result=JsonData.fail(e.getMessage());
                MV=new ModelAndView("jsonView",result.toMap());
            }else {
                logger.error("unknow json exception,url:"+url,e);
                JsonData result=JsonData.fail(defaultMsg);
                MV=new ModelAndView("jsonView",result.toMap());
            }
            //这里要求项目中所有请求page页面，都使用.page结尾
        }else if (url.endsWith(".page")){
            logger.error("unknow page exception,url:"+url,e);
            JsonData result=JsonData.fail(defaultMsg);
            MV=new ModelAndView("exception",result.toMap());
        }else {
            logger.error("unknow exception,url:"+url,e);
            JsonData result=JsonData.fail(defaultMsg);
            MV=new ModelAndView("jsonView",result.toMap());
        }


        return MV;
    }
}
