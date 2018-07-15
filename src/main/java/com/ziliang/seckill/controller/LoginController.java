package com.ziliang.seckill.controller;

import com.ziliang.seckill.domain.SeckillUser;
import com.ziliang.seckill.redis.RedisService;
import com.ziliang.seckill.result.Result;
import com.ziliang.seckill.service.SeckillUserService;
import com.ziliang.seckill.vo.LoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "登录controller", tags = {"登录操作"})
@RestController
@CrossOrigin(origins = {"http://localhost:8081"}, allowCredentials = "true")
@RequestMapping("/login")
public class LoginController {
    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    SeckillUserService userService;

    @Autowired
    RedisService redisService;

//    @GetMapping("/to_login")
//    public String toLogin() {
//        return "login";
//    }

    @ApiOperation(value = "执行登录操作", notes = "接收mobile和password进行登录操作")
    @ApiImplicitParam(name = "loginVo", value = "登录参数", required = true, dataType = "LoginVo")
    @PostMapping("/do_login")
    public Result<SeckillUser> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        log.info(loginVo.toString());
        //登录
        SeckillUser user = userService.login(response, loginVo);
        return Result.success(user);
    }
}
