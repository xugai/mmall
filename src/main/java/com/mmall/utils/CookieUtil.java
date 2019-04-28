package com.mmall.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by rabbit on 2019/1/6.
 */
@Slf4j
public class CookieUtil {
    private final static String COOKIE_DOMAIN = ".immall.tk";
    private final static String COOKIE_NAME = "mmall_login_token";

    public static String readLoginToken(HttpServletRequest request){
        Cookie [] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(!StringUtils.isEmpty(cookie.getName()) && cookie.getName().equals(COOKIE_NAME)){
//                    log.info("match cookie {} in client.", COOKIE_NAME);
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


    public static void writeLoginToken(String token, HttpServletResponse response){
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");    //代表设置在根目录
        //设置cookie在客户端的存活时间，单位是秒
        //若该属性不设置的话，cookie就不会写入硬盘，而是存在内存中，仅在当前页面中有效
        cookie.setMaxAge(60 * 60 * 24 * 365);
        cookie.setHttpOnly(true);
        log.info("store cookie: name {}, value {} in client.", COOKIE_NAME, token);
        response.addCookie(cookie);
    }
}
