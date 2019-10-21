package com.biyesheji.dao;

import com.biyesheji.pojo.Role;
import com.biyesheji.pojo.RoleExample;

import java.util.List;

public interface RoleMapper extends SysDao<Role> {

    List<Role> selectByExample(RoleExample example);

}