package com.example.springboot_restful.mapper;

import com.example.springboot_restful.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper{

    List<User> findAll();

    User findByUsername(String username);

    User findByUserId(Integer id);

    User findByUid(String uid);

    User findByEmail(String email);

    User registerByUsername(String username);

    // 真删除
    int deleteByIdT(Integer id);

    // 假删除
    int deleteByIdF(Integer id);

    int update(User user);

    List<User> selectPage(Integer pageNum, Integer pageSize, String username, String email, String address, Integer deleted);

    // 查询总条数
    Integer selectTotal(String username, String email, String address, Integer deleted);

    // 插入（批量）
    int saveBatch(List<User> list);

    User login(String username, String password);

    int saveUser(User user);
}
