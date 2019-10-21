package com.biyesheji.dao;

import com.biyesheji.pojo.ZiXun;
import com.biyesheji.pojo.ZiXunExample;

import java.util.List;

public interface ZiXunMapper extends CrudDao<ZiXun> {

    List<ZiXun> selectByExample(ZiXunExample example);

    /**
     * 资讯审核
     * @param zid
     */
    void shenhe(int zid);

}