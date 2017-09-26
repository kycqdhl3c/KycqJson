package com.kycq.library.json.adapter;

import com.kycq.library.json.Json;
import com.kycq.library.json.JsonException;
import com.kycq.library.json.creator.ObjectConstructor;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonToken;
import com.kycq.library.json.stream.JsonWriter;

import java.lang.reflect.Type;
import java.util.Collection;

public class CollectionTypeAdapter<E> extends TypeAdapter<Collection<E>> {
	private final TypeAdapter<E> elementTypeAdapter;
	private final ObjectConstructor<? extends Collection<E>> constructor;
	
	public CollectionTypeAdapter(Json json,
	                             Type elementType, TypeAdapter<E> elementTypeAdapter,
	                             ObjectConstructor<? extends Collection<E>> constructor) {
		this.elementTypeAdapter = new TypeAdapterRuntimeTypeWrapper<E>(json, elementTypeAdapter, elementType);
		this.constructor = constructor;
	}
	
	@Override
	public Collection<E> read(JsonReader jsonReader) throws JsonException {
		JsonToken jsonToken = jsonReader.next();
		if (jsonToken == JsonToken.BEGIN_ARRAY) {
			Collection<E> collection = constructor.construct();
			JsonException jsonException = null;
			jsonReader.beginArray();
			while (jsonReader.hasNext()) {
				if (jsonException != null) {
					jsonReader.skipValue();
					continue;
				}
				try {
					E instance = elementTypeAdapter.read(jsonReader);
					collection.add(instance);
				} catch (JsonException e) {
					jsonException = e;
				}
			}
			jsonReader.endArray();
			if (jsonException != null) {
				return null;
			}
			return collection;
		} else {
			jsonReader.skipValue();
		}
		return null;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, Collection<E> value) throws JsonException {
		if (value == null) {
			jsonWriter.valueNull();
			return;
		}
		
		jsonWriter.beginArray();
		for (E element : value) {
			elementTypeAdapter.write(jsonWriter, element);
		}
		jsonWriter.endArray();
	}
}
