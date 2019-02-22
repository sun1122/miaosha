package com.flysun.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.flysun.miaosha.domain.MiaoshaUser;
import com.flysun.miaosha.redis.RedisService;
import com.flysun.miaosha.service.MiaoshaUserService;

@Controller
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	MiaoshaUserService userService;

	@Autowired
	RedisService redisService;

	@RequestMapping("/to_list")
	public String list(Model model,MiaoshaUser user) {
		model.addAttribute("user", user);
		return "goods_list";
	}

}