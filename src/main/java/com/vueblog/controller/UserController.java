package com.vueblog.controller;


import com.vueblog.common.lang.Result;
import com.vueblog.entity.User;
import com.vueblog.service.UserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 深林中的书海
 * @since 2021-02-05
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequiresAuthentication//必须登录认证才能访问的注解
    @GetMapping("/index")
    public  Object index(){
        User user = userService.getById(1);
        return Result.succ(user);
    }
    /**
     *
     *@RequestBody主要用来接收前端传递给后端的json字符串中的数据的(请求体中的数据的)；
     * GET方式无请求体，所以使用@RequestBody接收数据时，
     * 前端不能使用GET方式提交数据，
     * 而是用POST方式进行提交。在后端的同一个接收方法里，
     * @RequestBody与@RequestParam()可以同时使用，@RequestBody最多只能有一个，
     * 而@RequestParam()可以有多个。
     *
     * @Validated注解用于检查user中填写的规则  如果不满足抛出异常
     * 可在GlobalExceptionHandler中捕获此异常 进行自定义 返回数据信息
     */
    @PostMapping("/save")
    public  Result save(@Validated @RequestBody User user){

        return Result.succ(user);
    }


}
