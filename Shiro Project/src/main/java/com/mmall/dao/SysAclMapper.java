package com.mmall.dao;

import com.mmall.beans.PageQuery;
import com.mmall.model.SysAcl;
import com.mmall.model.SysAclExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclMapper {
    int deleteByExample(SysAclExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SysAcl record);

    int insertSelective(SysAcl record);

    SysAcl selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SysAcl record, @Param("example") SysAclExample example);

    int updateByExample(@Param("record") SysAcl record, @Param("example") SysAclExample example);

    int updateByPrimaryKeySelective(SysAcl record);

    int updateByPrimaryKey(SysAcl record);

    //根据权限模块id查出的权限点数量
    int countByAclModuleId(@Param("aclModuleId") int aclModuleId);

    //根据权限模块id获取当前页的权限点列表
    List<SysAcl> getPageByAclModuleId(@Param("aclModuleId") int aclModuleId, @Param("page") PageQuery page);

    //根据权限点名和权限模块id查找是否已存在权限点
    int countByNameAndAclModuleId(@Param("aclModuleId") int aclModuleId,@Param("name") String name,@Param("id") Integer id);

    //获取所有权限
    List<SysAcl> getAll();

    //根据权限id查出权限列表
    List<SysAcl> getByIdList(@Param("idList") List<Integer> idList);

    //通过url查找权限
    List<SysAcl> getByUrl(@Param("url") String url);

}