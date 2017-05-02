package com.kycq.library.json.adapter;

import com.kycq.library.json.Json;
import com.kycq.library.json.JsonElement;
import com.kycq.library.json.JsonException;
import com.kycq.library.json.JsonPrimitive;
import com.kycq.library.json.creator.ObjectConstructor;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonToken;
import com.kycq.library.json.stream.JsonWriter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapTypeAdapter<K, V> extends TypeAdapter<Map<K, V>> {
	private final TypeAdapter<K> keyTypeAdapter;
	private final TypeAdapter<V> valueTypeAdapter;
	private final ObjectConstructor<? extends Map<K, V>> constructor;
	
	public MapTypeAdapter(Json json,
	                      Type keyType, TypeAdapter<K> keyTypeAdapter,
	                      Type valueType, TypeAdapter<V> valueTypeAdapter,
	                      ObjectConstructor<? extends Map<K, V>> constructor) {
		this.keyTypeAdapter = new TypeAdapterRuntimeTypeWrapper<K>(json, keyTypeAdapter, keyType);
		this.valueTypeAdapter = new TypeAdapterRuntimeTypeWrapper<V>(json, valueTypeAdapter, valueType);
		this.constructor = constructor;
	}
	
	@SuppressWarnings("ThrowableInstanceNeverThrown")
	@Override
	public Map<K, V> read(JsonReader jsonReader) throws JsonException {
		JsonToken jsonToken = jsonReader.next();
		if (jsonToken == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		} else if (jsonToken == JsonToken.BEGIN_OBJECT) {
			Map<K, V> map = constructor.construct();
			JsonException jsonException = null;
			jsonReader.beginObject();
			while (jsonReader.hasNext()) {
				if (jsonException != null) {
					jsonReader.skipValue();
					continue;
				}
				try {
					jsonReader.nameToValue();
					K key = keyTypeAdapter.read(jsonReader);
					V value = valueTypeAdapter.read(jsonReader);
					map.put(key, value);
				} catch (JsonException e) {
					jsonException = new JsonException(e);
				}
			}
			jsonReader.endObject();
			if (jsonException != null) {
				return null;
			}
			return map;
		} else {
			jsonReader.skipValue();
		}
		return null;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, Map<K, V> value) throws JsonException {
		if (value == null) {
			jsonWriter.valueNull();
			return;
		}
		
		boolean hasComplexKeys = false;
		List<JsonElement> keys = new ArrayList<>(value.size());
		
		List<V> values = new ArrayList<>(value.size());
		for (Map.Entry<K, V> entry : value.entrySet()) {
			JsonElement keyElement = keyTypeAdapter.toJsonTree(entry.getKey());
			keys.add(keyElement);
			values.add(entry.getValue());
			hasComplexKeys |= keyElement.isJsonArray() || keyElement.isJsonObject();
		}
		
		if (hasComplexKeys) {
			jsonWriter.beginArray();
			for (int index = 0, size = keys.size(); index < size; index++) {
				jsonWriter.beginArray();
				JsonElementAdapter.ADAPTER.write(jsonWriter, keys.get(index));
				valueTypeAdapter.write(jsonWriter, values.get(index));
				jsonWriter.endArray();
			}
			jsonWriter.endArray();
		} else {
			jsonWriter.beginObject();
			for (int index = 0, size = keys.size(); index < size; index++) {
				JsonElement keyElement = keys.get(index);
				jsonWriter.name(keyToString(keyElement));
				valueTypeAdapter.write(jsonWriter, values.get(index));
			}
			jsonWriter.endObject();
		}
	}
	
	private String keyToString(JsonElement keyElement) {
		if (keyElement.isJsonPrimitive()) {
			JsonPrimitive primitive = keyElement.getAsJsonPrimitive();
			if (primitive.isNumber()) {
				return String.valueOf(primitive.getAsNumber());
			} else if (primitive.isBoolean()) {
				return Boolean.toString(primitive.getAsBoolean());
			} else if (primitive.isString()) {
				return primitive.getAsString();
			} else {
				throw new AssertionError();
			}
		} else if (keyElement.isJsonNull()) {
			return "null";
		} else {
			throw new AssertionError();
		}
	}
}
