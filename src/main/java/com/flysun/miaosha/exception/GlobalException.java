
package com.flysun.miaosha.exception;

import com.flysun.miaosha.result.CodeMsg;

/**
 * @ClassName: GlobalException
 * @Description: TODO(自定义的全局异常)
 * @author 周家申
 * @date 2019年2月19日
 * 
 */
public class GlobalException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private CodeMsg cm;

	public GlobalException(CodeMsg cm) {
		super(cm.toString());
		this.cm = cm;
	}

	public CodeMsg getCm() {
		return cm;
	}
}
