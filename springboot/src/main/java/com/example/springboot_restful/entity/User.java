package com.example.springboot_restful.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User implements Serializable {

    private Integer id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String uid;
    private String avatar;
    private String address;
    private String role;
    private Integer deleted;
    private LocalDateTime create_time;
    private LocalDateTime update_time;
}
