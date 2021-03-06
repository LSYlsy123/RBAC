package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysDeptMapper;
import com.mmall.dao.SysUserMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysDept;
import com.mmall.param.DeptParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpUtil;
import com.mmall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class SysDeptService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysLogService sysLogService;

    //保存部门的方法
    public void save(DeptParam param){
        BeanValidator.check(param);
        if (checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的部门");
        }
        SysDept dept=SysDept.builder().name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).remark(param.getRemark()).build();

        dept.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        dept.setOperator(RequestHolder.getCurrentUser().getUsername());
        dept.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        dept.setOperateTime(new Date());
        sysDeptMapper.insertSelective(dept);
        sysLogService.saveDeptLog(null,dept);
    }

    //更新部门的方法
    public void update(DeptParam param){

        BeanValidator.check(param);
        if (checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的部门");
        }
        SysDept before=sysDeptMapper.selectByPrimaryKey(param.getId());
        //确保不为空
        Preconditions.checkNotNull(before,"待更新的部门不存在");
        if (checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的部门");
        }
        //更新后的部门
        SysDept after=SysDept.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).remark(param.getRemark()).build();
        after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());

        updateWithChild(before,after);
        sysLogService.saveDeptLog(before,after);

    }

    @Transactional
    private void updateWithChild(SysDept before,SysDept after){
        String newLevelPrefix=after.getLevel();
        String oldLevelPrefix=before.getLevel();
        //判断是否需要更新子部门
        if (!after.getLevel().equals(before.getLevel())){
            List<SysDept> deptList=sysDeptMapper.getChildDeptListByLevel(before.getLevel());
            //子部门不为空才处理
            if (CollectionUtils.isNotEmpty(deptList)){
                for (SysDept dept:deptList){
                    //子部门的level
                    String level=dept.getLevel();
                    //如果以之前部门level前缀的话
                    if (level.indexOf(oldLevelPrefix)==0){
                        //子部门前缀是新的部门前缀，后面的不变
                        level=newLevelPrefix+level.substring(oldLevelPrefix.length());
                        dept.setLevel(level);
                    }
                }
                sysDeptMapper.batchUpdateLevel(deptList);
            }
        }

        sysDeptMapper.updateByPrimaryKeySelective(after);

    }

    //判断部门是否已经重复
    //Integer id 可为空，因为新增部门不传部门id，更新是要根据部门id更新
    private boolean checkExist(Integer parentId,String deptName,Integer deptId){
        return sysDeptMapper.countByNameAndParenId(parentId,deptName,deptId)>0;
    }

    //拿level值
    private String getLevel(Integer deptId){
        SysDept dept=sysDeptMapper.selectByPrimaryKey(deptId);
        if (dept==null){
            return null;
        }
        return dept.getLevel();
    }

    public void delete(int deptId){
        SysDept dept=sysDeptMapper.selectByPrimaryKey(deptId);
        Preconditions.checkNotNull(dept,"待删除部门不存在，无法删除");
        if (sysDeptMapper.countByParentId(dept.getId())>0){
            throw new ParamException("当前部门下有子部门，无法删除");
        }
        if (sysUserMapper.countByDeptId(dept.getId())>0){
            throw new ParamException("当前部门下有用户，无法删除");
        }
        sysDeptMapper.deleteByPrimaryKey(deptId);
    }
}
