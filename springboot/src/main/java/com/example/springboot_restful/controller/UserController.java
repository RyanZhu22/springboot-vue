package com.example.springboot_restful.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.springboot_restful.common.ResultBody;
import com.example.springboot_restful.common.error.BusinessException;
import com.example.springboot_restful.common.error.CommonEnum;
import com.example.springboot_restful.common.error.ResultEnum;
import com.example.springboot_restful.common.handler.MsgException;
import com.example.springboot_restful.controller.dto.UserDTO;
import com.example.springboot_restful.common.JsonResult;
import com.example.springboot_restful.entity.User;
import com.example.springboot_restful.mapper.UserMapper;
import com.example.springboot_restful.service.UserService;
import com.example.springboot_restful.utils.TokenUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @GetMapping("/id")
    public ResultBody findOne(@RequestParam("id") Integer id) {
        User user = userMapper.findByUserId(id);
        return ResultBody.success(user);
    }

    @PostMapping("/update")
    public ResultBody update(User user) {
        int update = userMapper.update(user);
        if (update != 1) {
            return ResultBody.error("500", "Internet Error");
        }

        return ResultBody.success();
    }

    @GetMapping
    public List<User> findAll() {
        List<User> all = userMapper.findAll();
        return all;
    }

    // RequestBody ?????????JSON???????????????Java??????
    @PostMapping
    public ResultBody save(@RequestBody User user) throws ResultBody {
        // ???????????????
        return userService.save(user);
    }

    /**
     * ????????????
     */
    @PostMapping("/login")
    public ResultBody login(@RequestBody UserDTO userDTO) throws ResultBody {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        // hutool??????username????????????
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return ResultBody.error("500", "Username is empty");
        }
        UserDTO dto = userService.login(userDTO);

        return ResultBody.success(dto);
    }

    /**
     * ????????????
     * @param user
     * @return
     */
    @PostMapping("/register")
    public ResultBody register(@RequestBody User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        // hutool??????username????????????
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return ResultBody.error("500", "username or password is null");
        }

        if (userMapper.registerByUsername(user.getUsername()) == null) {
            userMapper.insert(user);
            return ResultBody.success();
        } else {
            return ResultBody.error("500", "Username already exists");
        }
    }

    // ???????????????
    @DeleteMapping(value = "/{id}")
    public Integer deleteByIdT(@PathVariable("id") Integer id) {
        return userMapper.deleteByIdT(id);
    }

    // ???????????????
    @PostMapping(value = "/{id}")
    public Integer deleteByIdF(@PathVariable("id") Integer id) {
        return userMapper.deleteByIdF(id);
    }

    // ??????????????????
    // ??????????????? /user/page?pageNum=1&pageSize=10
    // LIMIT (pageNum - 1) * pageSize
    @GetMapping("/page")
    public Map<String, Object> findPage(@RequestParam Integer pageNum,
                                        @RequestParam Integer pageSize,
                                        @RequestParam(defaultValue = "") String username,
                                        @RequestParam(defaultValue = "") String email,
                                        @RequestParam(defaultValue = "") String address,
                                        @RequestParam("deleted") Integer deleted) {
        pageNum = (pageNum - 1) * pageSize;
        List<User> data = userMapper.selectPage(pageNum, pageSize, username, email, address, deleted);
        Integer total = userMapper.selectTotal(username, email, address, deleted);
        Map<String, Object> res = new HashMap<>();
        res.put("data", data);
        res.put("total", total);

        return res;
    }

    // ???????????? Excel poi??????
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // ????????????????????????
        List<User> list = userMapper.findAll();
        // ?????????????????????????????????
        ExcelWriter writer = ExcelUtil.getWriter(true);
        // ?????????????????????
        writer.setOnlyAlias(true);
        writer.addHeaderAlias("username", "username");
        writer.addHeaderAlias("password", "password");
        writer.addHeaderAlias("nickname", "nickname");
        writer.addHeaderAlias("email", "email");
        writer.addHeaderAlias("phone", "phone");
        writer.addHeaderAlias("address", "address");
        writer.addHeaderAlias("createTime", "createTime");
        writer.addHeaderAlias("avatarUrl", "avatarUrl");

        // ???????????????list????????????excel, ???????????????????????????????????????
        writer.write(list, true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("User-Information", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream outputStream = response.getOutputStream();
        writer.flush(outputStream, true);
        outputStream.close();
        writer.close();
    }

    /**
     * excel ??????
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public int imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<User> list = reader.readAll(User.class);
        System.out.println(list);
        int result = userService.saveBatch(list);
        return result;
    }

}
