package com.kycq.library.json.adapter;

import com.kycq.library.json.Json;
import com.kycq.library.json.reflect.TypeToken;

public class ObjectTypeAdapterFactory implements TypeAdapterFactory {
	public static final TypeAdapterFactory FACTORY = new ObjectTypeAdapterFactory();
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> TypeAdapter<T> create(Json json, TypeToken<T> typeToken) {
		if (typeToken.getRawType() == Object.class) {
			return (TypeAdapter<T>) new ObjectTypeAdapter(json);
		}
		return null;
	}
}
