package com.ziliang.seckill.controller;

import com.ziliang.seckill.domain.SeckillUser;
import com.ziliang.seckill.redis.RedisService;
import com.ziliang.seckill.result.Result;
import com.ziliang.seckill.service.SeckillUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "用户controller", tags = {"获取用户信息"})
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    SeckillUserService userService;

    @Autowired
    RedisService redisService;

    @ApiOperation(value = "获取用户信息")
    @GetMapping("/info")
    public Result<SeckillUser> info(Model model, SeckillUser user) {
        return Result.success(user);
    }
}
