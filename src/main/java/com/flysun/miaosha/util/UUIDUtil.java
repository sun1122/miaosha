
package com.flysun.miaosha.util;

import java.util.UUID;

/**  
 * @ClassName: UUIDUtil  
 * @Description: TODO(UUIDUtil 工具类)  
 * @author 周家申  
 * @date 2019年2月22日  
 *    
 */
public class UUIDUtil {
	//private static Logger log = LoggerFactory.getLogger(UUIDUtil.class);
	/**
	 * 
	 * @Title: uuid  
	 * @Description: TODO(生成uuid，去除-)  
	 * @param @return    参数  
	 * @return String    返回类型  
	 * @throws
	 */
	public static String uuid() {
		
		return UUID.randomUUID().toString().replace("-", "");
	}
}


