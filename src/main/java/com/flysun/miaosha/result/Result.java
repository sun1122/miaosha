/**    
 * @Description: TODO(用一句话描述该文件做什么)  
 * @author 周家申  
 * @date 2019年1月25日    
 */
package com.flysun.miaosha.result;

/**
 * @ClassName: Result
 * @Description: TODO(结果封装)
 * @author 周家申
 * @date 2019年1月25日
 * 
 */
public class Result<T> {

	private int code;
	private String msg;
	private T data;

	/**
	 * 
	 * @Title: success @Description: TODO(成功之后的调用) @param @param msg @param @return
	 *         参数 @return Result<T> 返回类型 @throws
	 */
	public static <T> Result<T> success(T data) {
		return new Result<T>(data);
	}

	/**
	 * 
	 * @Title: error @Description: TODO(失败的时候调用) @param @param msg @param @return
	 *         参数 @return Result<T> 返回类型 @throws
	 */
	public static <T> Result<T> error(CodeMsg cm) {

		return new Result<T>(cm);

	}

	/**
	 * 创建一个新的实例 Result.
	 * 
	 * @param data
	 */
	private Result(T data) {
		this.code = 0;
		this.msg = "success";
		this.data = data;
	}

	/**
	 * 创建一个新的实例 Result.
	 * 
	 * @param cm
	 */
	private Result(CodeMsg cm) {
		if (cm == null) {
			return;
		}
		this.code = cm.getCode();
		this.msg = cm.getMsg();

	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public T getData() {
		return data;
	}

}
