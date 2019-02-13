
package com.flysun.miaosha.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.flysun.miaosha.util.ValidatorUtil;

/**
 * @ClassName: IsMobileValidator
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author 周家申
 * @date 2019年2月12日
 * 
 *       IsMobile, String 注解 以及注解修饰的字段的类型
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {
	
	
	private boolean required = false;


	@Override
	public void initialize(IsMobile constraintAnnotation) {
		required = constraintAnnotation.required();
		
	}

	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if(required) {
			return ValidatorUtil.isMobile(value);
		}else {
			if(StringUtils.isEmpty(value)) {
				return true;
			}else {
				return ValidatorUtil.isMobile(value);
			}
		}
	}

}
