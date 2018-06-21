package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysAclMapper;
import com.mmall.dao.SysAclModuleMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysAclModule;
import com.mmall.model.SysDept;
import com.mmall.param.AclModuleParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpUtil;
import com.mmall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysAclModuleService {

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    @Resource
    private SysAclMapper sysAclMapper;

    @Resource
    private SysLogService sysLogService;

    public void save(AclModuleParam param){
        BeanValidator.check(param);
        if (checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的权限模块");
        }
        SysAclModule aclModule=SysAclModule.builder().name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).status(param.getStatus()).remark(param.getRemark()).build();
        aclModule.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        aclModule.setOperator(RequestHolder.getCurrentUser().getUsername());
        aclModule.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        aclModule.setOperateTime(new Date());
        sysAclModuleMapper.insertSelective(aclModule);
        sysLogService.saveAclModuleLog(null,aclModule);
    }

    public void update(AclModuleParam param){
        BeanValidator.check(param);
        if (checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的权限模块");
        }
        //检查传入的id是否能查找到实体类
        SysAclModule before=sysAclModuleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的权限模块不存在");

        SysAclModule after=SysAclModule.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).status(param.getStatus()).remark(param.getRemark()).build();
        after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        updateWithChild(before,after);
        sysLogService.saveAclModuleLog(before,after);
    }

    @Transactional
    private void updateWithChild(SysAclModule before,SysAclModule after){

        String newLevelPrefix=after.getLevel();
        String oldLevelPrefix=before.getLevel();
        //判断是否需要更新子部门
        if (!after.getLevel().equals(before.getLevel())){
            List<SysAclModule> aclModuleList=sysAclModuleMapper.getChildAclModuleListByLevel(before.getLevel());
            //子部门不为空才处理，这里处理批量更新子模块
            if (CollectionUtils.isNotEmpty(aclModuleList)){
                for (SysAclModule aclModule:aclModuleList){
                    String level=aclModule.getLevel();
                    if (level.indexOf(oldLevelPrefix)==0){
                        level=newLevelPrefix+level.substring(oldLevelPrefix.length());
                        aclModule.setLevel(level);
                    }
                }
                sysAclModuleMapper.batchUpdateLevel(aclModuleList);
            }
        }

        sysAclModuleMapper.updateByPrimaryKeySelective(after);
    }

    private boolean checkExist(Integer parentId,String aclModuleName,Integer deptId){
        return sysAclModuleMapper.countByNameAndParenId(parentId,aclModuleName,deptId)>0;
    }

    //拿level值
    private String getLevel(Integer aclModuleId){
        SysAclModule aclModule=sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        if (aclModule==null){
            return null;
        }
        return aclModule.getLevel();
    }

    public void delete(int aclModuleId){
        SysAclModule aclModule=sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        Preconditions.checkNotNull(aclModule,"待删除待权限模块不存在，无法删除");
        if (sysAclModuleMapper.countByParentId(aclModuleId)>0){
            throw new ParamException("当前模块下有子模块，无法删除");
        }
        if (sysAclMapper.countByAclModuleId(aclModuleId)>0){
            throw new ParamException("当前模块下有权限点，无法删除");
        }
        sysAclModuleMapper.deleteByPrimaryKey(aclModuleId);
    }

}
