
package com.flysun.miaosha.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.flysun.miaosha.domain.User;

/**  
 * @ClassName: UserDao  
 * @Description: TODO(数据库访问层)  
 * @author 周家申  
 * @date 2019年1月26日  
 *    
 */
@Mapper
public interface UserDao {
	
	@Select("select * from user where id = #{id}")
	public User getUserById(@Param("id") int id);


	@Insert("insert into user (id ,name ) values (#{id},#{name})")
	public int insert(User user);

}


