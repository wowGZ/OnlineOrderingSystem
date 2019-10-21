package com.biyesheji.dao;

import com.biyesheji.pojo.Permission;
import com.biyesheji.pojo.PermissionExample;

import java.util.List;

public interface PermissionMapper extends SysDao<Permission>{

    List<Permission> selectByExample(PermissionExample example);

}