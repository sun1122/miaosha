package com.flysun.miaosha.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.flysun.miaosha.domain.MiaoshaUser;
import com.flysun.miaosha.redis.RedisService;
import com.flysun.miaosha.service.GoodsService;
import com.flysun.miaosha.service.MiaoshaUserService;
import com.flysun.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	MiaoshaUserService userService;

	@Autowired
	RedisService redisService;
	
	@Autowired
	GoodsService goodsService;

	@RequestMapping("/to_list")
	public String list(Model model, MiaoshaUser user) {
		model.addAttribute("user", user);
		//查询商品列表
    	List<GoodsVo> goodsList = goodsService.listGoodsVo();
    	model.addAttribute("goodsList", goodsList);
    	
		return "goods_list";
	}

}
