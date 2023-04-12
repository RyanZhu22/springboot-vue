package com.example.springboot_restful.service.impl;

import com.example.springboot_restful.entity.RolePermission;
import com.example.springboot_restful.mapper.RolePermissionMapper;
import com.example.springboot_restful.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public int deleteByRoleId(Integer roleId) {
        return rolePermissionMapper.deleteByRoleId(roleId);
    }

    @Override
    public int insert(RolePermission rolePermission) {
        return rolePermissionMapper.insert(rolePermission);
    }

    @Transactional
    @Override
    public void setRoleMenu(Integer roleId, List<Integer> menuIds) {
        // 先删除当前角色id所有绑定关系
        this.deleteByRoleId(roleId);

        // 再把前端传输的菜单id数组绑定在当前角色id
        for (Integer menuId : menuIds) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRole_id(roleId);
            rolePermission.setPermission_id(menuId);
            this.insert(rolePermission);
        }
    }

    @Override
    public List<Integer> selectByRoleId(Integer roleId) {
        return rolePermissionMapper.selectByRoleId(roleId);
    }

    @Override
    public List<Integer> getRoleMenu(Integer roleId) {
        return this.selectByRoleId(roleId);
    }

    @Override
    public List<RolePermission> findAll() {
        return rolePermissionMapper.findAll();
    }


}
