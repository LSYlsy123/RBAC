package com.mmall.dao;

import com.mmall.model.SysDept;
import com.mmall.model.SysDeptExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDeptMapper {
    int deleteByExample(SysDeptExample example);

    int deleteByPrimaryKey(@Param("id") Integer id);

    int insert(SysDept record);

    int insertSelective(SysDept record);

    SysDept selectByPrimaryKey(@Param("id") Integer id);

    int updateByExampleSelective(@Param("record") SysDept record, @Param("example") SysDeptExample example);

    int updateByExample(@Param("record") SysDept record, @Param("example") SysDeptExample example);

    int updateByPrimaryKeySelective(SysDept record);

    int updateByPrimaryKey(SysDept record);

    //获取所有部门列表
    List<SysDept> getAllDept();

    //获取子部门列表
    List<SysDept> getChildDeptListByLevel(@Param("level") String level);

    //批量更新level
    void batchUpdateLevel(@Param("sysDeptList") List<SysDept> sysDeptList);

    //判断部门是否已经存在
    int countByNameAndParenId(@Param("parentId") Integer parenId,@Param("name") String name,@Param("id") Integer id );

    //判断部门下是否有子部门
    int countByParentId(@Param("deptId") int deptId);
}