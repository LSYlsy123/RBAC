package com.mmall.dto;

import com.google.common.collect.Lists;
import com.mmall.model.SysAclModule;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;
@Getter
@Setter
@ToString
public class AclModuleLevelDto extends SysAclModule {

    //下面继续包含自己，组成树形结构
    private List<AclModuleLevelDto> aclModuleList= Lists.newArrayList();

    //权限模块下关系权限点
    private List<AclDto> aclList=Lists.newArrayList();

    //传入SysDept转化为当前结构
    public static AclModuleLevelDto adapt(SysAclModule aclModule){

        AclModuleLevelDto dto=new AclModuleLevelDto();
        //copy一个bean
        BeanUtils.copyProperties(aclModule,dto);
        return dto;
    }


}
