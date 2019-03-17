package com.flysun.miaosha.config;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.flysun.miaosha.domain.MiaoshaUser;
import com.flysun.miaosha.service.MiaoshaUserService;
/**
 * 
 * @ClassName: UserArgumentResolver  
 * @Description: TODO(HandlerMethod 方法中MiaoshaUser.class 参数解析器)  
 * @author 周家申  
 * @date 2019年2月22日  
 *
 */
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

	@Autowired
	MiaoshaUserService userService;
	/**
	 *  判断 HandlerMethodArgumentResolver 
	 *  是否支持 MethodParameter(PS: 一般都是通过 参数上面的注解|参数的类型)
	 * <p>Title: supportsParameter</p>  
	 * <p>Description: resolveArgument对MiaoshaUser类型有效</p>  
	 * @param parameter
	 * @return
	 */
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> clazz = parameter.getParameterType();
		return clazz==MiaoshaUser.class;
	}
	/**
	 * 
	 *  NativeWebRequest(其实就是HttpServletRequest) 中获取数据, 解决 方法上的参数
	 * <p>Title: resolveArgument</p>  
	 * <p>Description: 参数解析以及封装赋值</p>  
	 * @param parameter
	 * @param mavContainer
	 * @param webRequest
	 * @param binderFactory
	 * @return
	 * @throws Exception
	 */
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
		
		//参数cookie
		String paramToken = request.getParameter(MiaoshaUserService.COOKIE_NAME_TOKEN);
		//cookie
		String cookieToken = getCookieValue(request, MiaoshaUserService.COOKIE_NAME_TOKEN);
		
		if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
			return null;
		}
		//参数cookie覆盖 cookie
		String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
		//以cookie获取用户信息
		return userService.getByToken(response, token);
	}
    
	/**
	 * 
	 * @Title: getCookieValue  
	 * @Description: TODO(读取特定值的cookie信息)  
	 * @param @param request
	 * @param @param cookiName
	 * @param @return    参数  
	 * @return String    返回类型  
	 * @throws
	 */
	private String getCookieValue(HttpServletRequest request, String cookiName) {
		Cookie[]  cookies = request.getCookies();
		if(cookies == null || cookies.length <= 0) {
		    return null;
		}
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals(cookiName)) {
				return cookie.getValue();
			}
		}
		return null;
	}

}
