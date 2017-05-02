package com.kycq.library.json.adapter;

import com.kycq.library.json.Json;
import com.kycq.library.json.JsonElement;
import com.kycq.library.json.reflect.TypeToken;

public class JsonElementAdapterFactory implements TypeAdapterFactory {
	public static final TypeAdapterFactory FACTORY = new JsonElementAdapterFactory();
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> TypeAdapter<T> create(Json json, TypeToken<T> typeToken) {
		final Class<? super T> requestedType = typeToken.getRawType();
		if (!JsonElement.class.isAssignableFrom(requestedType)) {
			return null;
		}
		return (TypeAdapter<T>) JsonElementAdapter.ADAPTER;
	}
}
