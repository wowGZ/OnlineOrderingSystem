package com.biyesheji.dao;

import com.biyesheji.pojo.RolePermission;
import com.biyesheji.pojo.RolePermissionExample;

import java.util.List;

public interface RolePermissionMapper extends SysDao<RolePermission>{

    List<RolePermission> selectByExample(RolePermissionExample example);

}