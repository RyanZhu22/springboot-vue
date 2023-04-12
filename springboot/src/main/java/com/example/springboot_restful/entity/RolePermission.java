package com.example.springboot_restful.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolePermission {
    private Integer id;
    private Integer role_id;
    private Integer permission_id;
}
