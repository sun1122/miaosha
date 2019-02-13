package com.flysun.miaosha.controller;

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
import com.flysun.miaosha.service.UserService;

@Controller
@RequestMapping("/demo")
public class SampleController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private RedisService redisService;

	@RequestMapping("/")
	@ResponseBody
	String home() {
		return "Hello World!";
	}

	// 1.rest api json输出 2.页面
	@RequestMapping("/hello")
	@ResponseBody
	public Result<String> hello() {
		return Result.success("hello,imooc");
		// return new Result(0, "success", "hello,imooc");
	}

	@RequestMapping("/helloError")
	@ResponseBody
	public Result<String> helloError() {
		return Result.error(CodeMsg.SERVER_ERROR);
		// return new Result(500102, "XXX");
	}

	@RequestMapping("/thymeleaf")
	public String thymeleaf(Model model) {
		model.addAttribute("name", "flysun");
		return "hello";
	}

	@RequestMapping("/db/get")
	@ResponseBody
	public Result<User> dbGet() {
		User user = userService.getUserById(1);
		return Result.success(user);
		// return new Result(500102, "XXX");
	}

	@RequestMapping("/db/tx")
	@ResponseBody
	public Result<Boolean> dbTx() {
		userService.dbTx();
		return Result.success(true);
		// return new Result(500102, "XXX");
	}

	@RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {
    	User  user  = redisService.get(UserKey.getById, ""+1, User.class);
        return Result.success(user);
    }
    
    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {
    	User user  = new User();
    	user.setId(1);
    	user.setName("11112222");
    	redisService.set(UserKey.getById, ""+1, user);//UserKey:id1
        return Result.success(true);
    }

}
