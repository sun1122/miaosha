package com.flysun.miaosha.controller;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.flysun.miaosha.domain.User;
import com.flysun.miaosha.redis.RedisService;
import com.flysun.miaosha.redis.UserKey;
import com.flysun.miaosha.result.CodeMsg;
import com.flysun.miaosha.result.Result;
import com.flysun.miaosha.service.MiaoshaUserService;
import com.flysun.miaosha.service.UserService;
import com.flysun.miaosha.util.ValidatorUtil;
import com.flysun.miaosha.vo.LoginVo;

@Controller
@RequestMapping("/login")
public class LoginController {

	private static Logger log = LoggerFactory.getLogger(LoginController.class);
	@Autowired
	private MiaoshaUserService miaoshaUserService;

	@Autowired
	private RedisService redisService;

	@RequestMapping("/to_login")
	public String toLogin() {
		return "login";
	}

	@RequestMapping("/do_login")
	@ResponseBody
	public Result<Boolean> doLogin(@Valid LoginVo loginVo) {
		log.info(loginVo.toString());
		miaoshaUserService.login(loginVo);
		return Result.success(true);

	}
}
