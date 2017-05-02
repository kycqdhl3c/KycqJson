package com.kycq.library.json.adapter;

import com.kycq.library.json.Json;
import com.kycq.library.json.creator.ObjectConstructor;
import com.kycq.library.json.reflect.GsonTypes;
import com.kycq.library.json.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class MapTypeAdapterFactory implements TypeAdapterFactory {
	public static final MapTypeAdapterFactory FACTORY = new MapTypeAdapterFactory();
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> TypeAdapter<T> create(Json json, TypeToken<T> typeToken) {
		Type type = typeToken.getType();
		
		Class<? super T> rawType = typeToken.getRawType();
		if (!Map.class.isAssignableFrom(rawType)) {
			return null;
		}
		
		Class<?> rawTypeOfSrc = GsonTypes.getRawType(type);
		Type[] keyAndValueTypes = GsonTypes.getMapKeyAndValueTypes(type, rawTypeOfSrc);
		TypeAdapter<?> keyAdapter = json.getAdapter(TypeToken.get(keyAndValueTypes[0]));
		TypeAdapter<?> valueAdapter = json.getAdapter(TypeToken.get(keyAndValueTypes[1]));
		ObjectConstructor<T> constructor = json.getConstructorCreator().get(typeToken);
		
		return new MapTypeAdapter(json, keyAndValueTypes[0], keyAdapter,
				keyAndValueTypes[1], valueAdapter, constructor);
	}
}
