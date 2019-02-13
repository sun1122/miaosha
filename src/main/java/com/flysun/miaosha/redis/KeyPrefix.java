
package com.flysun.miaosha.redis;

/**
 * @ClassName: KeyPrefix
 * @Description: TODO(通用缓存key)
 * @author 周家申
 * @date 2019年1月27日
 * 
 */
public interface KeyPrefix {

	public int expireSeconds();

	public String getPrefix();
}
