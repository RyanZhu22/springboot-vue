package com.example.springboot_restful.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.example.springboot_restful.common.Constants;
import com.example.springboot_restful.common.enums.EmailCodeEnum;
import com.example.springboot_restful.controller.dto.LoginDTO;
import com.example.springboot_restful.controller.dto.UserRequest;
import com.example.springboot_restful.entity.Permission;
import com.example.springboot_restful.entity.Role;
import com.example.springboot_restful.entity.User;
import com.example.springboot_restful.exception.ServiceException;
import com.example.springboot_restful.mapper.UserMapper;
import com.example.springboot_restful.service.PermissionService;
import com.example.springboot_restful.service.RolePermissionService;
import com.example.springboot_restful.service.RoleService;
import com.example.springboot_restful.service.UserService;
import com.example.springboot_restful.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    /**
     * 登录业务逻辑
     * @param user
     * @return
     */
    @Override
    public LoginDTO login(UserRequest user) {
        User dbUser;
        try {
            dbUser = userMapper.findByUsername(user.getUsername());
        } catch (Exception e) {
            throw new RuntimeException("Database exception");
        }
        // verity the user
        if (dbUser == null) {
            throw new ServiceException("The user is not found");
        }

        // 查询用户的菜单树（2层）
        String flag = dbUser.getRole();
        List<Permission> all = getPermissions(flag); // 水平

        // 页面菜单权限
        List<Permission> menus = getTreePermissions(all); // 树形
        // 页面按钮权限
        List<Permission> auths = all.stream().filter(permission -> permission.getType() == 3)
                .collect(Collectors.toList());
        log.info(all.toString(), menus, auths);
        // 返回登录数据
        return LoginDTO.builder().user(dbUser).menus(menus).auths(auths).build();
    }

    /**
     * 主页业务逻辑
     * @param user
     */
    @Override
    public void register(UserRequest user) {
        // save email key and value
//        String emailKey = Constants.EMAIL_CODE + EmailCodeEnum.REGISTER.getValue() + user.getEmail();
//        RedisUtils.setCacheObject(emailKey, user.getEmailCode());
        try {
            User saveUser = new User();
            // 把请求数据的属性copy给存储数据库的属性
            BeanUtils.copyProperties(user, saveUser);
            // 存储用户信息
            saveUser(saveUser);
        } catch (Exception e) {
            throw new RuntimeException("Database exception", e);
        }
    }

    @Override
    public void logout(String uid) {
        // current session logout
//        StpUtil.logout(uid);
        log.info("User {} exit successful", uid);
    }

    // TODO Why check email in passwordReset and check uid in passwordChange
    // The user doesn't know their current password, so we use email and check email
    @Override
    public String passwordReset(UserRequest user) {
        // validate user through check email
        String email = user.getEmail();
        User dbUser = userMapper.findByEmail(email);
        if (dbUser == null) {
            throw new ServiceException("The user is not found");
        }
        // TODO why we should validate email validation code

        // new password
        String newPwd = "123";
        // set new password to dbUser
//        dbUser.setPassword(BCrypt.hashpw(newPwd));
        // save into database
        try {
            update(dbUser);
        } catch (Exception e) {
            throw new RuntimeException("Register Failed", e);
        }
        return newPwd;
    }

    // TODO Why check email in passwordReset and check uid in passwordChange
    // The user doesn't know their current password
    @Override
    public void passwordChange(UserRequest user) {
        // validate user through uid
        User dbUser = userMapper.findByUid(user.getUid());
        if (dbUser == null) {
            throw new ServiceException("The user is not found");
        }
//        boolean checkPwd = BCrypt.checkpw(user.getPassword(), dbUser.getPassword());
//        if (!checkPwd) {
//            throw new ServiceException("The old password is incorrect");
//        }
        // get the new password
        String newPwd = user.getNewPassword();
        // encrypted password
//        dbUser.setPassword(BCrypt.hashpw(newPwd));
        update(dbUser); // update database
    }

    @Override
    public User saveUser(User user) {
        // 校验username是否唯一
        User dbUser = userMapper.findByUsername(user.getUsername());
        if (dbUser != null) {
            throw new ServiceException("用户已存在");
        }
        // 校验是否有name
        if (StrUtil.isBlank(user.getName())) {
            // 设置昵称
            user.setName("system_user" + RandomUtil.randomString(6));
        }
        // 校验是否有password
        if (StrUtil.isBlank(user.getPassword())) {
            // 设置默认密码
            user.setPassword(Constants.DEFAULT_PASSWORD);
        }
        // 加密用户密码
//        user.setPassword(BCrypt.hashpw(user.getPassword())); // BCrypt hash加密
        // 设置uid 唯一标识
        user.setUid(IdUtil.fastSimpleUUID());
        try {
            userMapper.saveUser(user);
        } catch (Exception e) {
            throw new RuntimeException("Register Failed", e);
        }
        return user;
    }

    /**
     * 校验邮箱
     * @param emailKey
     * @param emailCode
     */
    @Override
    public void validateEmail(String emailKey, String emailCode) {
        Integer code = RedisUtils.getCacheObject(emailKey);
        if (code == null) {
            throw new ServiceException("Verification code is invalid");
        }
        if (!emailCode.equals(code.toString())) {
            throw new ServiceException("验证码错误");
        }
        RedisUtils.deleteObject(emailKey); // 清除缓存
    }

    private List<Permission> getPermissions(String roleFlag) {
        // 通过唯一标识Flag获取role
        Role role = roleService.getByFlag(roleFlag);
        // 通过roleId获取对应的permissions
        List<Integer> rolePermissions = rolePermissionService.selectByRoleId(role.getId());
        List<Permission> permissionList = permissionService.findAll();
        List<Permission> all = new ArrayList<>(); // 水平的菜单树
        for (Integer permissionId : rolePermissions) {
            permissionList.stream().filter(permission -> permission.getId().equals(permissionId))
                    .findFirst().ifPresent(all::add);
        }
        return all;
    }

    // 获取角色对应的菜单树
    private List<Permission> getTreePermissions(List<Permission> all) {
        // 菜单树 1级 -> 2级
        List<Permission> parentList = all.stream().filter(permission -> permission.getType() == 1)
                .collect(Collectors.toList());// type == 1是目录
        for (Permission permission : parentList) {
            Integer pid = permission.getId();
            List<Permission> level2List = all.stream().filter(permission1 -> pid.equals(permission1.getPid()))
                    .collect(Collectors.toList());// 2级菜单
            permission.setChildren(level2List);
        }
        return parentList;
    }

    // 批量删除
    public int saveBatch(List<User> list) {
        try {
            return userMapper.saveBatch(list);
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public User findByUserId(Integer id) {
        return userMapper.findByUserId(id);
    }

    @Override
    public User findByUid(String uid) {
        return userMapper.findByUid(uid);
    }

    @Override
    public User registerByUsername(String username) {
        return userMapper.registerByUsername(username);
    }

    @Override
    public int deleteByIdT(Integer id) {
        return userMapper.deleteByIdT(id);
    }

    @Override
    public int deleteByIdF(Integer id) {
        return userMapper.deleteByIdF(id);
    }

    @Override
    public int update(User user) {
        return userMapper.update(user);
    }

    @Override
    public List<User> selectPage(Integer pageNum, Integer pageSize, String username, String email, String address, Integer deleted) {
        return userMapper.selectPage(pageNum, pageSize, username, email, address, deleted);
    }

    @Override
    public Integer selectTotal(String username, String email, String address, Integer deleted) {
        return userMapper.selectTotal(username, email, address, deleted);
    }

}