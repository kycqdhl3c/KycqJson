package com.kycq.library.json.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JsonIgnore {
	/**
	 * 是否序列化
	 *
	 * @return true进行序列化
	 */
	boolean serialize() default false;
	
	/**
	 * 是否反序列化
	 *
	 * @return true进行反序列化
	 */
	boolean deserialize() default false;
}
