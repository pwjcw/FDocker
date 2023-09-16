package com.pwjcw.service.impl;

import com.pwjcw.entity.User;
import com.pwjcw.mapper.UserMapper;
import com.pwjcw.service.UserService;
import com.pwjcw.utils.MyBatisUtil;

public class UserServiceImpl implements UserService {
    /**
     * 判断账户是否存在，以及密码是否正确,正确返回true，否则返回false
     * @param user
     * @return
     */
    @Override
    public boolean Login(User user){
        UserMapper mapper = MyBatisUtil.getSession().getMapper(UserMapper.class);
        User login = mapper.Login(user.getUsername());
        System.out.println(login);
        if (login==null){
            return false;
        }else {
            if (login.getPasswd().equals(user.getPasswd())){
                return true;
            }else {
                return false;
            }
        }
    }

    /**
     * 修改管理员密码
     * @param oldPasswd
     * @param newPasswd
     * @return
     */
    @Override
    public boolean changePasswd(String oldPasswd, String newPasswd) {
        UserMapper mapper = MyBatisUtil.getSession().getMapper(UserMapper.class);
        User admin = mapper.Login("admin");
        if (admin.getPasswd().equals(oldPasswd)){
            mapper.ChangePasswd(newPasswd);
            return true;
        }
        return false;
    }


}
