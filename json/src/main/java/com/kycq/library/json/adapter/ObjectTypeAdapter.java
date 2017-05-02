package com.kycq.library.json.adapter;

import com.kycq.library.json.Json;
import com.kycq.library.json.JsonException;
import com.kycq.library.json.internal.LinkedTreeMap;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonToken;
import com.kycq.library.json.stream.JsonWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ObjectTypeAdapter extends TypeAdapter<Object> {
	private Json json;
	
	public ObjectTypeAdapter(Json json) {
		this.json = json;
	}
	
	@Override
	public Object read(JsonReader jsonReader) throws JsonException {
		JsonToken jsonToken = jsonReader.next();
		switch (jsonToken) {
			case BEGIN_ARRAY:
				List<Object> list = new ArrayList<>();
				jsonReader.beginArray();
				while (jsonReader.hasNext()) {
					list.add(read(jsonReader));
				}
				jsonReader.endArray();
				return list;
			case BEGIN_OBJECT:
				Map<String, Object> map = new LinkedTreeMap<>();
				jsonReader.beginObject();
				while (jsonReader.hasNext()) {
					map.put(jsonReader.nextName(), read(jsonReader));
				}
				jsonReader.endObject();
				return map;
			case STRING:
				return jsonReader.nextString();
			case NUMBER:
				return jsonReader.nextDouble();
			case BOOLEAN:
				return jsonReader.nextBoolean();
			case NULL:
				jsonReader.nextNull();
				return null;
			default:
				throw new IllegalStateException();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void write(JsonWriter jsonWriter, Object value) throws JsonException {
		if (value == null) {
			jsonWriter.valueNull();
			return;
		}
		
		TypeAdapter<Object> typeAdapter = (TypeAdapter<Object>) this.json.getAdapter(value.getClass());
		if (typeAdapter instanceof ObjectTypeAdapter) {
			jsonWriter.beginObject();
			jsonWriter.endObject();
			return;
		}
		
		typeAdapter.write(jsonWriter, value);
	}
}
