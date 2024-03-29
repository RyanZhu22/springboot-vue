package com.example.springboot_restful.mapper;


import com.example.springboot_restful.entity.Role;
import com.example.springboot_restful.entity.RolePermission;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {

    int saveOrUpdate(Role role);

    int removeById(Integer id);

    Role getById(Integer id);

    int totalCount();

    List<Role> findPage(String name, Integer pageNum, Integer pageSize);

    List<Role> findAll();

    Role getByFlag(String flag);
}
