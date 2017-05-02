package com.kycq.library.json.adapter;

import com.kycq.library.json.Json;
import com.kycq.library.json.creator.ObjectConstructor;
import com.kycq.library.json.reflect.GsonTypes;
import com.kycq.library.json.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;

public class CollectionTypeAdapterFactory implements TypeAdapterFactory {
	public static final TypeAdapterFactory FACTORY = new CollectionTypeAdapterFactory();
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> TypeAdapter<T> create(Json json, TypeToken<T> typeToken) {
		Type type = typeToken.getType();
		Class<? super T> rawType = typeToken.getRawType();
		if (!Collection.class.isAssignableFrom(rawType)) {
			return null;
		}
		
		Type elementType = GsonTypes.getCollectionElementType(type, rawType);
		TypeAdapter<?> elementTypeAdapter = json.getAdapter(TypeToken.get(elementType));
		ObjectConstructor<T> constructor = json.getConstructorCreator().get(typeToken);
		
		return new CollectionTypeAdapter(json, elementType, elementTypeAdapter, constructor);
	}
}
