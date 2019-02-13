/**    
 * @Description: TODO(用一句话描述该文件做什么)  
 * @author 周家申  
 * @date 2019年1月27日    
 */  
package com.flysun.miaosha.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**  
 * @ClassName: RedisPoolFactory  
 * @Description: TODO(RedisPool 连接池)  
 * @author 周家申  
 * @date 2019年1月27日  
 *    
 */
@Service
public class RedisPoolFactory {

	@Autowired
	private RedisConfig redisConfig;
	
	@Bean
	public JedisPool jedisFactory() {

		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
		jedisPoolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
		jedisPoolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait() * 1000);

		JedisPool jedisPool = new JedisPool(jedisPoolConfig, redisConfig.getHost(), redisConfig.getPort(),
				redisConfig.getTimeout() * 1000, redisConfig.getPassword(), 0);

		return jedisPool;

	}
}


