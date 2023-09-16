package com.pwjcw.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"/docker","/images","/pagemanager","/user?method=tomodifyPasswdPage"})
public class AuthFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;


        HttpSession session = request.getSession(false);
        boolean isLoggedIn = session != null && session.getAttribute("user") != null;

        if (isLoggedIn) {
            // 已登录或访问登录页，放行请求
            filterChain.doFilter(request, response);
        } else {
            // 未登录且非登录页，重定向至index界面
            response.sendRedirect(request.getContextPath() + "/index");
        }
    }

    @Override
    public void destroy() {
    }
}
