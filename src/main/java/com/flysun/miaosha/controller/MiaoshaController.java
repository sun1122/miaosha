package com.flysun.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.flysun.miaosha.domain.MiaoshaOrder;
import com.flysun.miaosha.domain.MiaoshaUser;
import com.flysun.miaosha.domain.OrderInfo;
import com.flysun.miaosha.redis.RedisService;
import com.flysun.miaosha.result.CodeMsg;
import com.flysun.miaosha.service.GoodsService;
import com.flysun.miaosha.service.MiaoshaService;
import com.flysun.miaosha.service.MiaoshaUserService;
import com.flysun.miaosha.service.OrderService;
import com.flysun.miaosha.vo.GoodsVo;
/**
 *   秒杀控制器
 * @author 周家申
 * @date 2019/03/17
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	MiaoshaService miaoshaService;
	
	/**
	 * 秒杀
	 * @param model
	 * @param user
	 * @param goodsId
	 * @return
	 */
    @RequestMapping("/do_miaosha")
    public String list(Model model,MiaoshaUser user,
    		@RequestParam("goodsId")long goodsId) {
    	model.addAttribute("user", user);
    	if(user == null) {
    		return "login";
    	}
    	//判断库存
    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    	int stock = goods.getStockCount();
    	if(stock <= 0) {
    		model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
    		return "miaosha_fail";
    	}
    	//判断是否已经秒杀到了
    	MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
    	if(order != null) {
    		model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
    		return "miaosha_fail";
    	}
    	//减库存 下订单 写入秒杀订单
    	OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
    	model.addAttribute("orderInfo", orderInfo);
    	model.addAttribute("goods", goods);
        return "order_detail";
    }
}
