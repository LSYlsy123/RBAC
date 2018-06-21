package com.mmall.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.beans.LogType;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysLogMapper;
import com.mmall.dao.SysRoleAclMapper;
import com.mmall.model.SysAclModule;
import com.mmall.model.SysLogWithBLOBs;
import com.mmall.model.SysRoleAcl;
import com.mmall.util.IpUtil;
import com.mmall.util.JsonMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SysRoleAclService {

    @Resource
    private SysRoleAclMapper sysRoleAclMapper;



    @Resource
    private SysLogMapper sysLogMapper;

    public void changeRoleAcls(Integer roleId, List<Integer> aclIdList){
        //取出角色当前已经分配的权限
        List<Integer> originAclIdList=sysRoleAclMapper.getAclIdListByRoleIdList(Lists.newArrayList(roleId));
        //和之前一样没有任何更新，则什么都不做
        if (originAclIdList.size()== aclIdList.size()){
            //当前角色已分配都权限点
            Set<Integer> originAclIdSet= Sets.newHashSet(originAclIdList);
            //前端传入进来的权限点
            Set<Integer> aclIdSet= Sets.newHashSet(aclIdList);
            //判断是否一样
            originAclIdSet.removeAll(aclIdSet);
            if (CollectionUtils.isEmpty(originAclIdSet)){
                return;
            }
        }
        //不一样的话执行更新
        updateRoleAcls(roleId, aclIdList);
        saveRoleAclLog(roleId,originAclIdList,aclIdList);
    }

    @Transactional
    public void updateRoleAcls(int roleId, List<Integer> aclIdList){
        //先将旧的权限点删除
        sysRoleAclMapper.deleteByRoleId(roleId);

        //如果保存的权限点是空的话直接返回
        if (CollectionUtils.isEmpty(aclIdList)){
            return;
        }

        //遍历组成角色权限进行批量插入
        List<SysRoleAcl> roleAclList=Lists.newArrayList();
        for (Integer aclId:aclIdList){
            SysRoleAcl roleAcl=SysRoleAcl.builder().roleId(roleId).aclId(aclId).operator(RequestHolder.getCurrentUser().getUsername())
                    .operateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest())).operateTime(new Date()).build();
            roleAclList.add(roleAcl);
        }
        sysRoleAclMapper.batchInsert(roleAclList);
    }

    private void saveRoleAclLog(int roleId, List<Integer> before,List<Integer> after){
        SysLogWithBLOBs sysLog=new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE_ACL);
        sysLog.setTargetId(roleId);
        sysLog.setOldValue(before==null?"": JsonMapper.object2String(before));
        sysLog.setNewValue(after==null?"":JsonMapper.object2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }
}
