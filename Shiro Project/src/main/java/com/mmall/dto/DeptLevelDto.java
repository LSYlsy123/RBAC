package com.mmall.dto;

import com.google.common.collect.Lists;
import com.mmall.model.SysDept;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Getter
@Setter
@ToString
public class DeptLevelDto extends SysDept {

    //下面继续包含自己，组成树形结构
    private List<DeptLevelDto> deptList= Lists.newArrayList();

    //传入SysDept转化为当前结构
    public static DeptLevelDto adapt(SysDept dept){

        DeptLevelDto dto=new DeptLevelDto();
        //copy一个bean
        BeanUtils.copyProperties(dept,dto);
        return dto;
    }
}
