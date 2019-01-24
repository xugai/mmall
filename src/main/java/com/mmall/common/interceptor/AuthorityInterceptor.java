package com.mmall.common.interceptor;

import com.github.pagehelper.StringUtil;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JsonUtil;
import com.mmall.utils.ShardedRedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by rabbit on 2019/1/17.
 */
@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        String beanName = handlerMethod.getBean().getClass().getSimpleName();
        String handleMethodName = handlerMethod.getMethod().getName();

        // 测试拦截器拦截到的请求，分析请求里面的参数
        StringBuffer requestParameters = new StringBuffer();
        Map requestParametersMap = request.getParameterMap();
        Iterator iterator = requestParametersMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            String parameterName = (String) entry.getKey();
            String parameterValue = StringUtils.EMPTY;

            // request里面携带的参数的值，是一个 String[] 类型的值
            Object parameterValueObj = entry.getValue();
            if(parameterValueObj instanceof String[]){
                String [] value = (String [])parameterValueObj;
                parameterValue = Arrays.toString(value);
            }
            requestParameters.append(parameterName).append("=").append(parameterValue).append(" ");
        }

        log.info("拦截器拦截类名:{}, 方法名:{}, 请求传递的参数:{}", beanName, handleMethodName, requestParameters.toString());

        String loginToken = CookieUtil.readLoginToken(request);
        User user = null;
        if(StringUtil.isNotEmpty(loginToken)){
            String userStr = ShardedRedisPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userStr, User.class);
        }
        if(user == null || (user.getRole().intValue() != Const.ROLE.ROLE_ADMIN)){
            // 这里要添加 reset, 否则报异常 ‘getWriter() has been called for this response.’
//            response.reset();
            // 这里要设置编码, 否则会乱码
            response.setCharacterEncoding("UTF-8");
            // 这里要设置返回值的类型, 因为全部是json接口
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();

            if(user == null){
                // 由于富文本控件的特殊要求,因此对富文本操作的异常情况返回值进行特殊处理.这里面区分是否登录以及用户身份是否为管理员这两种情况.
                if(StringUtils.equals("ProductManageController", beanName) && StringUtils.equals("richtextImgUpload", handleMethodName)){
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "请使用管理员身份登录后再操作！");
                    out.print(JsonUtil.obj2String(resultMap));
                }else {
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(), "拦截器拦截,请使用管理员身份进行登录!")));
                }
            }else{
                if(StringUtils.equals("ProductManageController", beanName) && StringUtils.equals("richtextImgUpload", handleMethodName)) {
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "无权限操作！");
                    out.print(JsonUtil.obj2String(resultMap));
                }else {
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorCodeAndMessage(ResponseCode.NEED_LOGIN.getCode(), "拦截器拦截,请使用管理员身份进行登录!")));
                }
            }
            out.flush();
            out.close();
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
