package com.mmall.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.beans.LogType;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysLogMapper;
import com.mmall.dao.SysRoleUserMapper;
import com.mmall.dao.SysUserMapper;
import com.mmall.model.SysLogWithBLOBs;
import com.mmall.model.SysRoleUser;
import com.mmall.model.SysUser;
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
public class SysRoleUserService {

    @Resource
    private SysRoleUserMapper sysRoleUserMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysLogService sysLogService;

    @Resource
    private SysLogMapper sysLogMapper;

    //取出该选中角色对应的用户列表
    public List<SysUser> getListByRoleId(int roleId){
        //已分配该角色的用户id
        List<Integer> userIdList=sysRoleUserMapper.getUserIdListByRoleId(roleId);
        if (CollectionUtils.isEmpty(userIdList)){
            return Lists.newArrayList();
        }
        //根据用户id取用户列表
        return sysUserMapper.getByIdList(userIdList);
    }

    public void changeRoleUsers(int roleId,List<Integer> userIdList){
        //已分配该角色的用户
        List<Integer> originUserIdList=sysRoleUserMapper.getUserIdListByRoleId(roleId);
        //判断前端传过来的数据是否改变过
        if (originUserIdList.size()== userIdList.size()){
            Set<Integer> originUserIdSet= Sets.newHashSet(originUserIdList);
            Set<Integer> userIdSet= Sets.newHashSet(userIdList);
            originUserIdSet.removeAll(userIdSet);
            if (CollectionUtils.isEmpty(originUserIdSet)){
                return;
            }
        }
        updateRoleUsers(roleId, userIdList);
        saveRoleUserLog(roleId,originUserIdList,userIdList);
    }

    @Transactional
    private void updateRoleUsers(int roleId,List<Integer> userIdList){
        //先删除之前的角色
        sysRoleUserMapper.deleteByRoleId(roleId);
        //判断出入的userid是否为空
        if (CollectionUtils.isEmpty(userIdList)){
            return;
        }
        //遍历铸成roleUser，进行批量插入
        List<SysRoleUser> roleUserList=Lists.newArrayList();
        for(Integer userId:userIdList) {
            SysRoleUser roleUser = SysRoleUser.builder().roleId(roleId).userId(userId).operator(RequestHolder.getCurrentUser().getUsername())
                    .operateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest())).operateTime(new Date()).build();
            roleUserList.add(roleUser);
        }
        sysRoleUserMapper.batchInsert(roleUserList);
    }

    private void saveRoleUserLog(int roleId,List<Integer> before,List<Integer> after){
        SysLogWithBLOBs sysLog=new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE_USER);
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
