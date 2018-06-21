package com.mmall.service;

import com.google.common.collect.Lists;
import com.mmall.beans.CacheKeyConstants;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysAclMapper;
import com.mmall.dao.SysRoleAclMapper;
import com.mmall.dao.SysRoleUserMapper;
import com.mmall.model.SysAcl;
import com.mmall.model.SysUser;
import com.mmall.util.JsonMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysCoreService {

    @Resource
    private SysAclMapper sysAclMapper;
    @Resource
    private SysRoleUserMapper sysRoleUserMapper;
    @Resource
    private SysRoleAclMapper sysRoleAclMapper;
    @Resource
    private SysCacheService sysCacheService;

    //获取当前用户权限列表
    public List<SysAcl> getCurrentUserAclList(){
        //取出当前登录的用户id
        int userId= RequestHolder.getCurrentUser().getId();

        return getUserAclList(userId);
    }

    //当前角色已分配的权限点列表
    public List<SysAcl> getRoleAclList(int roleId){
        //根据角色id查出已被分配的权限点
        List<Integer> aclIdList=sysRoleAclMapper.getAclIdListByRoleIdList(Lists.<Integer>newArrayList(roleId));
        if (CollectionUtils.isEmpty(aclIdList)){
            return Lists.newArrayList();
        }
        //根据权限id返回权限对象
        return sysAclMapper.getByIdList(aclIdList);
    }

    //通过userId查找用户已有的权限点
    public List<SysAcl> getUserAclList(int userId){
        //如果为超级管理员，取出所有权限数据
        if (isSuperAdmin()){
            return sysAclMapper.getAll();
        }
        //根据用户id取出用户已被分配的角色的Id
        List<Integer> userRoleIdList=sysRoleUserMapper.getRoleIdListByUserId(userId);
        //如果当前用户没有被分配任何角色，返回空权限点列表
        if (CollectionUtils.isEmpty(userRoleIdList)){
            return Lists.newArrayList();
        }
        //角色对应的权限的总和
        List<Integer> userAclIdList=sysRoleAclMapper.getAclIdListByRoleIdList(userRoleIdList);
        //判断取出权限总和是否为空
        if (CollectionUtils.isEmpty(userAclIdList)){
            return Lists.newArrayList();
        }

        return sysAclMapper.getByIdList(userAclIdList);

    }

    //判断是否为超级管理员，拥有所有权限
    public boolean isSuperAdmin(){
        // 这里是我自己定义了一个假的超级管理员规则，实际中要根据项目进行修改
        // 可以是配置文件获取，可以指定某个用户，也可以指定某个角色
        SysUser user=RequestHolder.getCurrentUser();
        if (user.getMail().contains("admin")){
            return true;
        }
        return false;
    }

    //是否有权限访问该url
    public boolean hasUrlAcl(String url){
        if (isSuperAdmin()){
            return true;
        }
        List<SysAcl> aclList=sysAclMapper.getByUrl(url);
        //如果url中没有权限点，说明系统不关心是否有权限，直接返回可以访问
        if (CollectionUtils.isEmpty(aclList)){
            return true;
        }
        //获取当前用户拥有的权限
        List<SysAcl> userAclIdList=getCurrentUserAclListFromCache();
        Set<Integer> userAclIdSet=userAclIdList.stream().map(acl->acl.getId()).collect(Collectors.toSet());
        boolean hasValidAcl=false;
        //规则：只要有一个权限点有权限，我们就认为有权限
        for (SysAcl acl:aclList){
            //判断一个用户是否具有某个权限点的访问权限
            if (acl==null||acl.getStatus()!=1){//权限点无效
                continue;
            }
            hasValidAcl=true;
            if (userAclIdSet.contains(acl.getId())){
                return true;
            }
        }
        if (!hasValidAcl){
            return true;
        }
        return false;
    }

    public List<SysAcl> getCurrentUserAclListFromCache(){
        int userId=RequestHolder.getCurrentUser().getId();
        String cacheValue=sysCacheService.getFromCache(CacheKeyConstants.USER_ACLS,String.valueOf(userId));
        if (StringUtils.isBlank(cacheValue)){
            List<SysAcl> aclList=getCurrentUserAclList();
            if (CollectionUtils.isEmpty(aclList)){
                sysCacheService.saveCache(JsonMapper.object2String(aclList),600,CacheKeyConstants.USER_ACLS,String.valueOf(userId));
            }
            return aclList;
        }
        return JsonMapper.string2Object(cacheValue, new TypeReference<List<SysAcl>>() {
        });
    }

}
