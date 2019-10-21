package com.biyesheji.dao;

import com.biyesheji.pojo.UserRole;
import com.biyesheji.pojo.UserRoleExample;

import java.util.List;

public interface UserRoleMapper extends SysDao<UserRole>{

    List<UserRole> selectByExample(UserRoleExample example);

}