package com.kycq.library.json.adapter;

import com.kycq.library.json.Json;
import com.kycq.library.json.reflect.TypeToken;

public class EnumTypeAdapterFactory implements TypeAdapterFactory {
	public static final TypeAdapterFactory FACTORY = new EnumTypeAdapterFactory();
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> TypeAdapter<T> create(Json json, TypeToken<T> typeToken) {
		Class<? super T> rawType = typeToken.getRawType();
		if (!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class) {
			return null;
		}
		if (!rawType.isEnum()) {
			rawType = rawType.getSuperclass();
		}
		return (TypeAdapter<T>) new EnumTypeAdapter(rawType);
	}
}
