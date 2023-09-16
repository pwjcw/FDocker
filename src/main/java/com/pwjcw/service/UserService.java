package com.pwjcw.service;

import com.pwjcw.entity.User;

public interface UserService {
    boolean Login(User user);
    boolean changePasswd(String oldPasswd,String newPasswd);


}
