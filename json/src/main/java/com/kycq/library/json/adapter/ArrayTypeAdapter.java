package com.kycq.library.json.adapter;

import com.kycq.library.json.Json;
import com.kycq.library.json.JsonException;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonToken;
import com.kycq.library.json.stream.JsonWriter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ArrayTypeAdapter<E> extends TypeAdapter<Object> {
	private final Class<E> componentType;
	private final TypeAdapter<E> componentTypeAdapter;
	
	public ArrayTypeAdapter(Json json, TypeAdapter<E> componentTypeAdapter, Class<E> componentType) {
		this.componentTypeAdapter = new TypeAdapterRuntimeTypeWrapper<E>(json, componentTypeAdapter, componentType);
		this.componentType = componentType;
	}
	
	@Override
	public Object read(JsonReader jsonReader) throws JsonException {
		JsonToken jsonToken = jsonReader.next();
		if (jsonToken == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		} else if (jsonToken == JsonToken.BEGIN_ARRAY) {
			JsonException jsonException = null;
			List<E> list = new ArrayList<>();
			jsonReader.beginArray();
			while (jsonReader.hasNext()) {
				if (jsonException != null) {
					jsonReader.skipValue();
					continue;
				}
				try {
					E instance = componentTypeAdapter.read(jsonReader);
					list.add(instance);
				} catch (JsonException e) {
					jsonException = e;
				}
			}
			jsonReader.endArray();
			if (jsonException != null) {
				return null;
			}
			
			int size = list.size();
			Object array = Array.newInstance(componentType, size);
			for (int index = 0; index < size; index++) {
				Array.set(array, index, list.get(index));
			}
			return array;
		}
		
		jsonReader.skipValue();
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void write(JsonWriter jsonWriter, Object value) throws JsonException {
		if (value == null) {
			jsonWriter.valueNull();
			return;
		}
		
		jsonWriter.beginArray();
		for (int index = 0, length = Array.getLength(value); index < length; index++) {
			E item = (E) Array.get(value, index);
			componentTypeAdapter.write(jsonWriter, item);
		}
		jsonWriter.endArray();
	}
}
