package com.pwjcw.servlet.User;

import com.alibaba.fastjson.JSON;
import com.pwjcw.entity.User;
import com.pwjcw.service.UserService;
import com.pwjcw.service.impl.UserServiceImpl;
import com.pwjcw.servlet.base.ModelBaseServlet;
import com.pwjcw.utils.JsonUtils;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "UserService", value = "/user")
public class UserServlet extends ModelBaseServlet {
    private UserService userService=new UserServiceImpl();

    /**
     * 处理登入的请求
     * @param request
     * @param response
     * @throws IOException
     */
    public void login(HttpServletRequest request,HttpServletResponse response) throws IOException {
        User user= (User) JsonUtils.parseJsonToBean(request, User.class);
        boolean login = userService.Login(user);
        Map<String,String> map=new HashMap<>();
        if (login){
            HttpSession session = request.getSession(true);   //登入成功，创建session
            session.setAttribute("user", login);
            map.put("msg","1");
            JsonUtils.writeResult(response,map);    //登入成功，返回登入msg为1
        }else {
         map.put("msg","0");
         JsonUtils.writeResult(response,map);
        }
    }

    /**
     * 跳转到修改密码的界面
     * @param request
     * @param response
     * @throws IOException
     */
    public void tomodifyPasswdPage(HttpServletRequest request,HttpServletResponse response) throws IOException {
        processTemplate("admin/user/modifypasswd",request,response);
    }

    /**
     * 修改密码的功能实现
     * @param request
     * @param response
     * @throws IOException
     */
    public void modifypasswd(HttpServletRequest request,HttpServletResponse response) throws IOException {
        //首先判断请求是否存在session,如果不存在则跳转到登入界面
        HttpSession session = request.getSession(false);
        boolean isLoggedIn = session != null && session.getAttribute("user") != null;
        if (isLoggedIn) {
            // 已登录或访问登录页，放行请求
            JSONObject res = (JSONObject) JsonUtils.JsonToString(request);
            String oldPassword = res.getString("oldPassword");
            String newPassword = res.getString("newPassword");
            boolean b = userService.changePasswd(oldPassword, newPassword);
            Map<String,String> map=new HashMap<>();
            map.put("msg", String.valueOf(b));
            JsonUtils.writeResult(response,map);
        } else {
            // 未登录且非登录页，重定向至index界面
            processTemplate("login/login",request,response);
        }
    }

    /**
     * 退出登入,使sessoin失效
     * @param request
     * @param response
     * @throws IOException
     */
    public void logout(HttpServletRequest request,HttpServletResponse response) throws IOException {
        HttpSession session=request.getSession();
        session.invalidate(); //session失效
    }

}