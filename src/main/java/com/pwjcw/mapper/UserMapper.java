package com.pwjcw.mapper;

import com.pwjcw.entity.User;

public interface UserMapper {
    User Login(String username);
    void ChangePasswd(String newPasswd);
}
