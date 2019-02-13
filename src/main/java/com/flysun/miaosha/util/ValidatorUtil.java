
package com.flysun.miaosha.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**  
 * @ClassName: ValidatorUtil  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author 周家申  
 * @date 2019年1月28日  
 *    
 */
public class ValidatorUtil {

	private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");
	
	public static boolean isMobile(String str) {
		if(StringUtils.isEmpty(str)) {
			return false;
		}
		Matcher	matcher = mobile_pattern.matcher(str);
		
		return matcher.matches();
	}
	
	public static void main(String[] args) {
		
		System.out.println(ValidatorUtil.isMobile("12222222222"));
	}
}


