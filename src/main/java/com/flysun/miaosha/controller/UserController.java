package com.flysun.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.flysun.miaosha.domain.MiaoshaUser;
import com.flysun.miaosha.redis.RedisService;
import com.flysun.miaosha.result.Result;
import com.flysun.miaosha.service.MiaoshaUserService;

/**
 * 用户信息压测 测试
 * @author 周家申
 * @date 2019/03/17
 */
@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> info(Model model,MiaoshaUser user) {
        return Result.success(user);
    }
    
}
