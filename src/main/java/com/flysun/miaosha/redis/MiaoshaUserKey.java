  
package com.flysun.miaosha.redis;
/**  
 * @ClassName: MiaoShaUserKey  
 * @Description: TODO(MiaoShaUserKey redis key)  
 * @author 周家申  
 * @date 2019年2月22日  
 *    
 */
public class MiaoshaUserKey extends BasePrefix{

	/**
	 * 默认有效期2天
	 */
	public static final int TOKEN_EXPIRE = 3600*24 * 2;
	/**  
	 * 创建一个新的实例 MiaoShaUserKey.  
	 *  
	 * @param expireSeconds
	 * @param prefix  
	 */  
	public MiaoshaUserKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}

	public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE,"token");
}


