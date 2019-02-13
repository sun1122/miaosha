 
package com.flysun.miaosha.redis;
/**  
 * @ClassName: BasePrefix  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author 周家申  
 * @date 2019年1月27日  
 *    
 */
public abstract class BasePrefix implements KeyPrefix {

    private int expireSeconds;
	
	private String prefix;
	
	public BasePrefix(String prefix) {//0代表永不过期
		this(0, prefix);
	}
	
	public BasePrefix( int expireSeconds, String prefix) {
		this.expireSeconds = expireSeconds;
		this.prefix = prefix;
	}
	
	public int expireSeconds() {//默认0代表永不过期
		return expireSeconds;
	}

	public String getPrefix() {
		String className = getClass().getSimpleName();
		return className+":" + prefix;
	}

}


