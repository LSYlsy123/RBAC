package com.mmall.dao;

import com.mmall.model.SysRoleAcl;
import com.mmall.model.SysRoleAclExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleAclMapper {
    int deleteByExample(SysRoleAclExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleAcl record);

    int insertSelective(SysRoleAcl record);

    SysRoleAcl selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SysRoleAcl record, @Param("example") SysRoleAclExample example);

    int updateByExample(@Param("record") SysRoleAcl record, @Param("example") SysRoleAclExample example);

    int updateByPrimaryKeySelective(SysRoleAcl record);

    int updateByPrimaryKey(SysRoleAcl record);

    //根据角色id列表查出所有权限id列表
    List<Integer> getAclIdListByRoleIdList(@Param("roleIdList") List<Integer> roleIdList);

    //根据角色id删除权限点，角色id不能为空
    void deleteByRoleId(@Param("roleId") int roleId);

    //批量插入
    void batchInsert(@Param("roleAclList") List<SysRoleAcl> roleAclList);

    //通过权限点获取角色列表
    List<Integer> getRoleIdListByAclId(@Param("aclId") int aclId);

}