package com.example.gulimall.order.interceptor;

import com.example.common.constant.AuthServerConstant;
import com.example.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截去
 *
 * @author taoao
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberRespVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI();
        AntPathMatcher matcher = new AntPathMatcher();
        boolean match = matcher.match("/order/order/status/**", uri);
        boolean match1 = matcher.match("/payed/notify", uri);
        if (match || match1) {
            return true;
        }

        MemberRespVo attribute = (MemberRespVo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute != null) {
            // 用户登录了
            loginUser.set(attribute);
            return true;
        } else {
            // 没登录，就去登录
            request.getSession().setAttribute("msg", "请先进行登录。");
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
    }
}
