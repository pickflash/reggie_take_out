package com.Hao.reggie.filter;

import com.Hao.reggie.common.BaseContext;
import com.Hao.reggie.common.Result;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否完成登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;
        //1,获取本次请求的URI
        String requestURI = request.getRequestURI();

        log.info("拦截到请求：{}",requestURI);
        //2,判断本次请求是否需要处理
        String[] uris=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "user/sendMsg",
                "/user/login"
        };//这是不需要过滤处理的网址

        //3,如果不需要处理，直接放行
        boolean check=check(uris,requestURI);
        if(check){
            log.info("本次请求不需要处理：{}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4-1,判断PC端登陆状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee")!=null){
            /*log.info("用户已登录,用户id为：{}",request.getSession().getAttribute("employee"));*/
            Long id=(Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(id);
            filterChain.doFilter(request,response);
            return;
        }

        //4,判断移动端登陆状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("user")!=null){
            /*log.info("用户已登录,用户id为：{}",request.getSession().getAttribute("employee"));*/
            Long id=(Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(id);
            filterChain.doFilter(request,response);
            return;
        }
        //5,如果未登录则返回未登录结果，通过输出流的方式向客户端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
        return;


    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url:urls) {
            boolean match=PATH_MATCHER.match(url,requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }


}
