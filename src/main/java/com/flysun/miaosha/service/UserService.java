
package com.flysun.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flysun.miaosha.dao.UserDao;
import com.flysun.miaosha.domain.User;

/**
 * @ClassName: UserService
 * @Description: TODO(业务层)
 * @author 周家申
 * @date 2019年1月26日
 * 
 */
@Service
public class UserService {

	@Autowired
	private UserDao userDao;

	public User getUserById(int id) {

		return userDao.getUserById(id);
	}

	@Transactional
	public boolean dbTx() {
		User user1 = new User();
		user1.setId(2);
		user1.setName("TX");
		userDao.insert(user1);

		User user2 = new User();
		user2.setId(1);
		user2.setName("TX");
		userDao.insert(user2);
		return true;
	}
}
