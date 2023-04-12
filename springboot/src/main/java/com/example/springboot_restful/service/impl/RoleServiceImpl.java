package com.example.springboot_restful.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.example.springboot_restful.entity.Role;
import com.example.springboot_restful.entity.RolePermission;
import com.example.springboot_restful.mapper.RoleMapper;
import com.example.springboot_restful.service.RolePermissionService;
import com.example.springboot_restful.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Override
    public int saveOrUpdate(Role role) {
        return roleMapper.saveOrUpdate(role);
    }

    @Override
    public int removeById(Integer id) {
        return roleMapper.removeById(id);
    }

    @Override
    public Role getById(Integer id) {
        return roleMapper.getById(id);
    }

    @Override
    public Map<String, Object> findPage(String name, Integer pageNum, Integer pageSize) {
        int total = this.totalCount();
        pageNum = (pageNum - 1) * pageSize;
        List<Role> roleList = roleMapper.findPage(name, pageNum, pageSize);;

        List<RolePermission> rolePermissionList = rolePermissionService.findAll();
        // for each role list
        roleList.forEach(v -> {
            List<Integer> permissionIds = rolePermissionList.stream().filter(r -> r.getRole_id().equals(v.getId()))
                    .map(RolePermission::getPermission_id).collect(Collectors.toList());
            v.setPermissionIds(permissionIds);
        });

        // map格式放回数据
        Map<String, Object> res = new HashMap<>();
        res.put("total", total);
        res.put("data", roleList);
        return res;
    }

    @Override
    public int totalCount() {
        return roleMapper.totalCount();
    }

    @Override
    public List<Role> findAll() {
        return roleMapper.findAll();
    }

    @Transactional
    @Override
    public void savePermissions(Integer roleId, List<Integer> permissionIds) throws ServerException {
        // check if the data is empty
        if (CollUtil.isEmpty(permissionIds) || roleId == null) {
            throw new ServerException("数据不能为空");
        }
        // delete all relationship between role and permission according to roleId
        rolePermissionService.deleteByRoleId(roleId);
        // insert relationship data from front-end
        permissionIds.forEach(v -> {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRole_id(roleId);
            rolePermission.setPermission_id(v);
            rolePermissionService.insert(rolePermission);
        });
    }

    @Override
    public Role getByFlag(String flag) {
        return roleMapper.getByFlag(flag);
    }
}
