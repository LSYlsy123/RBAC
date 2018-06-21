package com.mmall.dao;

import com.mmall.model.SysRole;
import com.mmall.model.SysRoleExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper {
    int deleteByExample(SysRoleExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SysRole record, @Param("example") SysRoleExample example);

    int updateByExample(@Param("record") SysRole record, @Param("example") SysRoleExample example);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);

    //获取所有角色
    List<SysRole> getAll();

    //根据角色名查找是否已经存在
    int countByName(@Param("name") String name,@Param("id") Integer id);

    //根据角色id获取角色列表
    List<SysRole> getByIdList(@Param("idList") List<Integer> idList);
}