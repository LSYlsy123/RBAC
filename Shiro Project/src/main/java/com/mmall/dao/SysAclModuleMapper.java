package com.mmall.dao;

import com.mmall.model.SysAclModule;
import com.mmall.model.SysAclModuleExample;
import com.mmall.model.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclModuleMapper {
    int deleteByExample(SysAclModuleExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SysAclModule record);

    int insertSelective(SysAclModule record);

    SysAclModule selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SysAclModule record, @Param("example") SysAclModuleExample example);

    int updateByExample(@Param("record") SysAclModule record, @Param("example") SysAclModuleExample example);

    int updateByPrimaryKeySelective(SysAclModule record);

    int updateByPrimaryKey(SysAclModule record);

    //根据权限名和上级权限id查找数量，同一层级下存在相同名称的权限模块
    int countByNameAndParenId(@Param("parentId") Integer parentId,@Param("name") String name,@Param("id") Integer id);

    //根据level获取所有的子模块
    List<SysAclModule> getChildAclModuleListByLevel(@Param("level") String level);

    //批量更新level
    void batchUpdateLevel(@Param("sysAclModuleList") List<SysAclModule> sysAclModuleList);

    //获取全部的权限模块是有saclM
    List<SysAclModule> getAllAclModule();

    //判断权限模块下是否有子模块
    int countByParentId(@Param("aclModuleId") int aclModuleId);
}