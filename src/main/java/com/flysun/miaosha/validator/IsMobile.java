
package com.flysun.miaosha.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @ClassName: IsMobile
 * @Description: TODO(自定义注解--jsr303)
 * @author 周家申
 * @date 2019年2月12日
 * 
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { IsMobileValidator.class })
public @interface IsMobile {

	boolean required() default true;

	String message() default "手机号码格式错误";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
