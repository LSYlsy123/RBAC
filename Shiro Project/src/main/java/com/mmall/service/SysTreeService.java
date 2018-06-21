package com.mmall.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mmall.dao.SysAclMapper;
import com.mmall.dao.SysAclModuleMapper;
import com.mmall.dao.SysDeptMapper;
import com.mmall.dto.AclDto;
import com.mmall.dto.AclModuleLevelDto;
import com.mmall.dto.DeptLevelDto;
import com.mmall.model.SysAcl;
import com.mmall.model.SysAclModule;
import com.mmall.model.SysDept;
import com.mmall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理树形结构的service层
 */
@Service
public class SysTreeService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    @Resource
    private SysCoreService sysCoreService;

    @Resource
    private SysAclMapper sysAclMapper;

    public List<AclModuleLevelDto> userAclTree(int userId){
        //获取用户获得的权限点
        List<SysAcl> userAclList=sysCoreService.getUserAclList(userId);
        List<AclDto> aclDtoList=Lists.newArrayList();
        for (SysAcl acl : userAclList) {
            AclDto dto = AclDto.adapt(acl);
            dto.setChecked(true);
            dto.setHasAcl(true);
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);
    }

    public List<AclModuleLevelDto> roleTree(int roleId){
        //当前用户已分配的权限点
        List<SysAcl> userAclList=sysCoreService.getCurrentUserAclList();

        //当前角色分配的权限点
        List<SysAcl> roleAclList=sysCoreService.getRoleAclList(roleId);

        //存储当前系统所有权限点
        List<AclDto> aclDtoList=Lists.newArrayList();

        //存储用户已分配的权限Id
        Set<Integer> userAclIdSet=userAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());

        //存储角色已分配的权限Id
        Set<Integer> roleAclIdSet=roleAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());

        List<SysAcl> allAclList=sysAclMapper.getAll();

        //遍历所有权限点
        for (SysAcl acl : allAclList) {
            AclDto dto = AclDto.adapt(acl);
            //判断此权限是否在用户已分配权限内
            if (userAclIdSet.contains(acl.getId())) {
                //标记为有此权限操作
                dto.setHasAcl(true);
            }
            //判断此权限是否在角色已分配权限内
            if (roleAclIdSet.contains(acl.getId())) {
                //标记为需要选中
                dto.setChecked(true);
            }
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);
    }

    //把aclDtoList转化为树
    public List<AclModuleLevelDto> aclListToTree(List<AclDto> aclDtoList){
        if (CollectionUtils.isEmpty(aclDtoList)) {
            return Lists.newArrayList();
        }
        //权限模块树得到权限模块的列表
        List<AclModuleLevelDto> aclModuleLevelList = aclModuleTree();

        //根据权限模块id存储权限点
        Multimap<Integer, AclDto> moduleIdAclMap = ArrayListMultimap.create();
        for(AclDto acl : aclDtoList) {
            if (acl.getStatus() == 1) {
                moduleIdAclMap.put(acl.getAclModuleId(), acl);
            }
        }
        bindAclsWithOrder(aclModuleLevelList, moduleIdAclMap);
        return aclModuleLevelList;
    }

    //将权限点按顺序绑定到权限模块树上
    public void bindAclsWithOrder(List<AclModuleLevelDto> aclModuleLevelList,Multimap<Integer,AclDto> moduleIdAclMap){
        if (CollectionUtils.isEmpty(aclModuleLevelList)) {
            return;
        }
        //遍历当前层级
        for (AclModuleLevelDto dto : aclModuleLevelList) {
            //获取当前所有acl
            List<AclDto> aclDtoList = (List<AclDto>)moduleIdAclMap.get(dto.getId());
            if (CollectionUtils.isNotEmpty(aclDtoList)) {
                Collections.sort(aclDtoList, aclSeqComparator);
                dto.setAclList(aclDtoList);
            }
            bindAclsWithOrder(dto.getAclModuleList(), moduleIdAclMap);
        }
    }


    public List<AclModuleLevelDto> aclModuleTree(){
        //获取所有权限模块
        List<SysAclModule> aclModuleList = sysAclModuleMapper.getAllAclModule();
        //定义一个树形结构
        List<AclModuleLevelDto> dtoList = Lists.newArrayList();
        //遍历所有的权限模块
        for (SysAclModule aclModule : aclModuleList) {
            //添加到树形结构之前适配
            dtoList.add(AclModuleLevelDto.adapt(aclModule));
        }
        return aclModuleListToTree(dtoList);
    }

    public List<AclModuleLevelDto> aclModuleListToTree(List<AclModuleLevelDto> dtoList){

        if (CollectionUtils.isEmpty(dtoList)) {
            return Lists.newArrayList();
        }
        // level -> [aclmodule1, aclmodule2, ...] Map<String, List<Object>>
        Multimap<String, AclModuleLevelDto> levelAclModuleMap = ArrayListMultimap.create();
        List<AclModuleLevelDto> rootList = Lists.newArrayList();

        //根据level存放权限模块四月saclw
        for (AclModuleLevelDto dto : dtoList) {
            levelAclModuleMap.put(dto.getLevel(), dto);
            if (LevelUtil.ROOT.equals(dto.getLevel())) {
                rootList.add(dto);
            }
        }
        Collections.sort(rootList, aclModuleSeqComparator);
        transformAclModuleTree(rootList, LevelUtil.ROOT, levelAclModuleMap);
        return rootList;
    }

    public void transformAclModuleTree(List<AclModuleLevelDto> dtoList,String level,Multimap<String,AclModuleLevelDto> levelAclModuleMap){
        //遍历List
        for (int i = 0; i < dtoList.size(); i++) {
            //获取当前层
            AclModuleLevelDto dto = dtoList.get(i);
            //取出下一层级的列表
            String nextLevel = LevelUtil.calculateLevel(level, dto.getId());
            List<AclModuleLevelDto> tempList = (List<AclModuleLevelDto>) levelAclModuleMap.get(nextLevel);
            if (CollectionUtils.isNotEmpty(tempList)) {
                Collections.sort(tempList, aclModuleSeqComparator);
                //设置下一层的list
                dto.setAclModuleList(tempList);
                transformAclModuleTree(tempList, nextLevel, levelAclModuleMap);
            }
        }
    }






     public List<DeptLevelDto> deptTree(){

         //获取部门列表
         List<SysDept> deptList=sysDeptMapper.getAllDept();

         //转化为DeptLevelDto的list
         List<DeptLevelDto> dtoList= Lists.newArrayList();
         for (SysDept dept:deptList){
             DeptLevelDto dto=DeptLevelDto.adapt(dept);
             dtoList.add(dto);
         }
         return deptListToTree(dtoList);
     }

     //讲dtoList注成树形结构
     public List<DeptLevelDto> deptListToTree(List<DeptLevelDto> deptLevelList){

         //判断是否为空
         if (CollectionUtils.isEmpty(deptLevelList )){
             return Lists.newArrayList();
         }

         //level->[dept1,dept2...]。key对多value
         //Map<String,List<Object>>,不需要每次初始化list
         Multimap<String,DeptLevelDto> levelDeptMap= ArrayListMultimap.create();
         //顶级部门的List
         List<DeptLevelDto> rootList=Lists.newArrayList();

         //遍历传进来的deptLevelList，放入map中，同一level多个dept
         for (DeptLevelDto dto:deptLevelList){
             levelDeptMap.put(dto.getLevel(),dto);
             //如果当前部门是顶级部门，加到rootList中
             if (LevelUtil.ROOT.equals(dto.getLevel())){
                 rootList.add(dto);
             }
         }
         //按照seq从小到大排序
         Collections.sort(rootList, new Comparator<DeptLevelDto>() {
             @Override
             public int compare(DeptLevelDto o1, DeptLevelDto o2) {
                 return o1.getSeq()-o2.getSeq();
             }
         });

         //递归生成树
         transformDeptTree(rootList,LevelUtil.ROOT,levelDeptMap);
         return rootList;
     }

     //递归排序
    //level:0,1,all.  0->0.1,0.2
    //level:0.1
    //level:0.2
    public void transformDeptTree(List<DeptLevelDto> deptLevelList,String level,Multimap<String,DeptLevelDto> levelDeptMap){

         for (int i=0;i<deptLevelList.size();i++){
             //遍历该层每一个元素
             DeptLevelDto deptLevelDto=deptLevelList.get(i);

             //处理当前层级
             String nextLevel=LevelUtil.calculateLevel(level,deptLevelDto.getId());

             //处理下一层
             List<DeptLevelDto> tempDeptList=(List<DeptLevelDto>)levelDeptMap.get(nextLevel);

             //进行排序及相关处理
             if (CollectionUtils.isNotEmpty(tempDeptList)){
                 //排序
                 Collections.sort(tempDeptList,deptSeqComparator);

                 //设置下一层部门
                 deptLevelDto.setDeptList(tempDeptList);

                 //进入下一层处理
                 transformDeptTree(tempDeptList,nextLevel,levelDeptMap);
             }
        }
    }



    public Comparator<DeptLevelDto> deptSeqComparator=new Comparator<DeptLevelDto>() {
        @Override
        public int compare(DeptLevelDto o1, DeptLevelDto o2) {
            return o1.getSeq()-o2.getSeq();
        }
    };

    public Comparator<AclModuleLevelDto> aclModuleSeqComparator=new Comparator<AclModuleLevelDto>() {
        @Override
        public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
            return o1.getSeq()-o2.getSeq();
        }
    };

    public Comparator<AclDto> aclSeqComparator=new Comparator<AclDto>() {
        @Override
        public int compare(AclDto o1, AclDto o2) {
            return o1.getSeq()-o2.getSeq();
        }
    };

}
