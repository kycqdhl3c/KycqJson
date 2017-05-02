package com.kycq.library.json.annotation;

import com.kycq.library.json.adapter.TypeAdapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JsonAdapter {
	
	Class<? extends TypeAdapter> value();
}
