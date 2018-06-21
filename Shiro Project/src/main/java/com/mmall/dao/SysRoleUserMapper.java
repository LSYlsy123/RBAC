package com.mmall.dao;

import com.mmall.model.SysRoleUser;
import com.mmall.model.SysRoleUserExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleUserMapper {
    int deleteByExample(SysRoleUserExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleUser record);

    int insertSelective(SysRoleUser record);

    SysRoleUser selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SysRoleUser record, @Param("example") SysRoleUserExample example);

    int updateByExample(@Param("record") SysRoleUser record, @Param("example") SysRoleUserExample example);

    int updateByPrimaryKeySelective(SysRoleUser record);

    int updateByPrimaryKey(SysRoleUser record);

    //根据userId查角色id列表
    List<Integer> getRoleIdListByUserId(@Param("userId") int userId);

    //根据角色id查找用户id，已分配该角色的用户
    List<Integer> getUserIdListByRoleId(@Param("roleId") int roleId);

    //根据角色id删除角色
    void deleteByRoleId(@Param("roleId") int roleId);

    //批量插入角色用户
    void batchInsert(@Param("roleUserList") List<SysRoleUser> roleUserList);

    //根据角色id列表获取用户id列表
    List<Integer> getUserIdListByRoleIdList(@Param("roleIdList") List<Integer> roleIdList);


}