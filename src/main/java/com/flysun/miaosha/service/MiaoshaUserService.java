package com.flysun.miaosha.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flysun.miaosha.dao.MiaoshaUserDao;
import com.flysun.miaosha.domain.MiaoshaUser;
import com.flysun.miaosha.exception.GlobalException;
import com.flysun.miaosha.redis.RedisService;
import com.flysun.miaosha.result.CodeMsg;
import com.flysun.miaosha.util.MD5Util;
import com.flysun.miaosha.vo.LoginVo;



@Service
public class MiaoshaUserService {
	
	
	public static final String COOKI_NAME_TOKEN = "token";
	
	@Autowired
	MiaoshaUserDao miaoshaUserDao;
	
	@Autowired
	RedisService redisService;
	
	public MiaoshaUser getById(long id) {
		return miaoshaUserDao.getById(id);
	}

	/**  
	 * @Title: login  
	 * @Description: TODO(这里用一句话描述这个方法的作用)  
	 * @param @param loginVo    参数  
	 * @return void    返回类型  
	 * @throws  
	 */  
	public boolean login(LoginVo loginVo) {
		// TODO Auto-generated method stub
		if(loginVo == null) {
			throw new GlobalException(CodeMsg.SERVER_ERROR);
		}
		String mobile = loginVo.getMobile();
		String formPass = loginVo.getPassword();//一次MD5密码
		//判断手机号是否存在
		MiaoshaUser miaoshaUser =getById(Long.parseLong(mobile));
		if(miaoshaUser == null) {
			//return CodeMsg.MOBILE_NOT_EXIST;
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		String dbPass = miaoshaUser.getPassword();//两次MD5密码
		String saltDB = miaoshaUser.getSalt();//服务端 salt
		//由客户端密码计算出来数据库密码
		String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
		//验证密码
		if(!calcPass.equals(dbPass)) {
			//return CodeMsg.PASSWORD_ERROR;
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		}
		return true;
		
		
	}
	

//	public MiaoshaUser getByToken(HttpServletResponse response, String token) {
//		if(StringUtils.isEmpty(token)) {
//			return null;
//		}
//		MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
//		//延长有效期
//		if(user != null) {
//			addCookie(response, token, user);
//		}
//		return user;
//	}
	

//	public boolean login(HttpServletResponse response, LoginVo loginVo) {
//		if(loginVo == null) {
//			throw new GlobalException(CodeMsg.SERVER_ERROR);
//		}
//		String mobile = loginVo.getMobile();
//		String formPass = loginVo.getPassword();
//		//判断手机号是否存在
//		MiaoshaUser user = getById(Long.parseLong(mobile));
//		if(user == null) {
//			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
//		}
//		//验证密码
//		String dbPass = user.getPassword();
//		String saltDB = user.getSalt();
//		String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
//		if(!calcPass.equals(dbPass)) {
//			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
//		}
//		//生成cookie
//		String token	 = UUIDUtil.uuid();
//		addCookie(response, token, user);
//		return true;
//	}
	
//	private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
//		redisService.set(MiaoshaUserKey.token, token, user);
//		Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
//		cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
//		cookie.setPath("/");
//		response.addCookie(cookie);
//	}

}
