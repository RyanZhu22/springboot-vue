package com.example.springboot_restful.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_role_permission")
public class RolePermission {
    private Integer id;
    private Integer roleId;
    private Integer permissionId;
}
