package com.flysun.miaosha.redis;

public class UserKey extends BasePrefix{

	private UserKey(String prefix) {
		super(prefix);
	}
	public static UserKey getById = new UserKey("id");//UserKey:id1
	public static UserKey getByName = new UserKey("name");
}
