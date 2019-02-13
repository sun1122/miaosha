package com.flysun.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;
/**
 * 
 * @ClassName: MD5Util  
 * @Description: TODO(MD5Util 工具类)  
 * @author 周家申  
 * @date 2019年2月11日  
 *
 */
public class MD5Util {
	
	/**
	 * 
	 * @Title: md5  
	 * @Description: TODO(一次MD5)  
	 * @param @param src
	 * @param @return    参数  
	 * @return String    返回类型  
	 * @throws
	 */
	public static String md5(String src) {
		return DigestUtils.md5Hex(src);
	}
	/**
	 * 客户端 salt
	 */
	private static final String salt = "1a2b3c4d";
	
	/**
	 * 
	 * @Title: inputPassToFormPass  
	 * @Description: TODO(用户输入后一次MD5---表单加密)  
	 * @param @param inputPass
	 * @param @return    参数  
	 * @return String    返回类型  
	 * @throws
	 */
	public static String inputPassToFormPass(String inputPass) {
		String str = ""+salt.charAt(0)+salt.charAt(2) + inputPass +salt.charAt(5) + salt.charAt(4);
		
		return md5(str);
	}
	
	/**
	 * 
	 * @Title: formPassToDBPass  
	 * @Description: TODO(对传来的MD5二次加密+服务器端salt随机) 
	 * @param @param formPass
	 * @param @param salt
	 * @param @return    参数  
	 * @return String    返回类型  
	 * @throws
	 */
	public static String formPassToDBPass(String formPass, String salt) {
		String str = ""+salt.charAt(0)+salt.charAt(2) + formPass +salt.charAt(5) + salt.charAt(4);
		return md5(str);
	}
	
	/**
	 * 
	 * @Title: inputPassToDbPass  
	 * @Description: TODO(明文两次MD5)  
	 * @param @param inputPass
	 * @param @param saltDB
	 * @param @return    参数  
	 * @return String    返回类型  
	 * @throws
	 */
	public static String inputPassToDbPass(String inputPass, String saltDB) {
		String formPass = inputPassToFormPass(inputPass);
		String dbPass = formPassToDBPass(formPass, saltDB);
		return dbPass;
	}
	
	public static void main(String[] args) {
		//System.out.println(inputPassToFormPass("123456"));//d3b1294a61a07da9b49b6e22b2cbd7f9
//		System.out.println(formPassToDBPass(inputPassToFormPass("123456"), "1a2b3c4d"));
	System.out.println(inputPassToDbPass("123456", "1a2b3c4d"));//b7797cce01b4b131b433b6acf4add449
	}
	
}
