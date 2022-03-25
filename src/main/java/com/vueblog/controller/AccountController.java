package com.vueblog.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vueblog.common.dto.LoginDto;
import com.vueblog.common.lang.Result;
import com.vueblog.entity.User;
import com.vueblog.service.UserService;
import com.vueblog.util.JwtUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class AccountController {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;


    @RequestMapping("/login")
    public Result login(@Validated @RequestBody LoginDto loginDto, HttpServletResponse response) throws IOException {
        User user = userService.getOne(new QueryWrapper<User>().eq("username", loginDto.getUsername()));
        //Assert.notNull(user,"用户不存在");//断言拦截
        if(user==null) {
            user = new User();
            user.setUsername(loginDto.getUsername());
            user.setPassword(loginDto.getPassword());
            user.setStatus(0);
            userService.save(user);
//            String result="注册成功，欢迎你";
//            ServletOutputStream outputStream = response.getOutputStream();
//            outputStream.write(result.getBytes());
        }
        //判断账号密码是否错误 因为是md5加密所以这里md5判断,数据库中应该存储md5加密后的密码
//        if(!user.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))){
        if(!user.getPassword().equals(loginDto.getPassword())){
            //密码不同则抛出异常
//            System.out.println(SecureUtil.md5(loginDto.getPassword()));
//            System.out.println(loginDto.getPassword());
//            System.out.println(user.getPassword());
            return Result.fail("密码不正确");
        }
        String jwt = jwtUtils.generateToken(user.getId());

        //将token 放在我们的header里面
        response.setHeader("Authorization",jwt);
        response.setHeader("Access-control-Expose-Headers","Authorization");

        return Result.succ(MapUtil.builder()
                .put("id",user.getId())
                .put("username",user.getUsername())
                .put("avatar",user.getAvatar())
                .put("email",user.getEmail()).map()

        );
    }

    //需要认证权限才能退出登录
    @RequiresAuthentication
    @RequestMapping("/logout")
    public Result logout() {
        //退出登录
        SecurityUtils.getSubject().logout();
        return Result.succ("注销成功");
    }



}
