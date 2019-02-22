package com.flysun.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.druid.util.StringUtils;
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
	public String list(Model model,
			@CookieValue(value = MiaoshaUserService.COOKIE_NAME_TOKEN, required = false) String cookieToken,
			@RequestParam(value = MiaoshaUserService.COOKIE_NAME_TOKEN, required = false) String paramToken) {
		if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
			return "login";
		}
		//优先使用 url token 
		String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
		
		MiaoshaUser user = userService.getByToken(token);
		model.addAttribute("user", user);
		return "goods_list";
	}

}
