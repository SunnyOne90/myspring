package com.gaofeng.spring.formework.annotation;

import java.lang.annotation.*;

/**
 * 请求url
 * @author Tom
 *
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GFRequestMapping {
	String value() default "";
}
