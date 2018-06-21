package com.mmall.common;

import com.mmall.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
public class HttpInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger= LoggerFactory.getLogger(HttpInterceptor.class);

    private static final String START_TIME="requestStartTime";

    //处理请求之前调用
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url=request.getRequestURI().toString();
        //当前请求的参数
        Map parameterMap=request.getParameterMap();
        logger.info("request start. url:{},params:{}",url, JsonMapper.object2String(parameterMap));
        long start=System.currentTimeMillis();
        request.setAttribute(START_TIME,start);
        return true;
    }

    //处理正常请求之后调用
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        String url=request.getRequestURI().toString();
//        Map parameterMap=request.getParameterMap();
//        long start=(long) request.getAttribute(START_TIME);
//        long end=System.currentTimeMillis();
//        logger.info("request finished. url:{},cost:{}",url, end-start);
        removeThreadLocalInfo();
    }

    //任何请求处理之后调用
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String url=request.getRequestURI().toString();
        //当前请求的参数
        Map parameterMap=request.getParameterMap();
        long start=(long) request.getAttribute(START_TIME);
        long end=System.currentTimeMillis();
        logger.info("request completed. url:{},cost:{}",url, end-start);
        removeThreadLocalInfo();

    }

    public void removeThreadLocalInfo(){
        RequestHolder.remove();
    }
}
