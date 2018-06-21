package com.mmall.dao;

import com.mmall.beans.PageQuery;
import com.mmall.model.SysUser;
import com.mmall.model.SysUserExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {
    int deleteByExample(SysUserExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SysUser record, @Param("example") SysUserExample example);

    int updateByExample(@Param("record") SysUser record, @Param("example") SysUserExample example);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    //登录时的校验，邮箱或手机登录
    SysUser findByKeyword(@Param("keyword") String keyword);

    //检查邮箱是否重复
    int countByMail(@Param("mail") String mail,@Param("id") Integer id);

    //检查手机号是否重复
    int countByTelephone(@Param("telephone") String telephone,@Param("id") Integer id);

    //根据部门id获取用户列表数
    int countByDeptId(@Param("deptId") int deptId);

    //根据部门id获取分页的用户列表
    List<SysUser> getPageByDeptId(@Param("deptId") int deptId, @Param("page") PageQuery page);

    //根据用户id查找用户列表
    List<SysUser> getByIdList(@Param("idList") List<Integer> idList);

    //获取所有的用户列表
    List<SysUser> getAll();

}