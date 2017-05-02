package com.kycq.library.json.adapter;

import com.kycq.library.json.Json;
import com.kycq.library.json.reflect.GsonTypes;
import com.kycq.library.json.reflect.TypeToken;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

public class ArrayTypeAdapterFactory implements TypeAdapterFactory {
	public static final TypeAdapterFactory FACTORY = new ArrayTypeAdapterFactory();
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> TypeAdapter<T> create(Json json, TypeToken<T> typeToken) {
		Type type = typeToken.getType();
		if (!(type instanceof GenericArrayType
				|| (type instanceof Class && ((Class<?>) type).isArray()))) {
			return null;
		}
		
		Type componentType = GsonTypes.getArrayComponentType(type);
		TypeAdapter<?> componentTypeAdapter = json.getAdapter(TypeToken.get(componentType));
		return new ArrayTypeAdapter(json, componentTypeAdapter, GsonTypes.getRawType(componentType));
	}
}
