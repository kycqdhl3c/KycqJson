package com.kycq.library.json.adapter;

import com.kycq.library.json.JsonException;
import com.kycq.library.json.JsonLog;
import com.kycq.library.json.creator.ObjectConstructor;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonToken;
import com.kycq.library.json.stream.JsonWriter;

import java.util.Map;

public class ReflectiveTypeAdapter<T> extends TypeAdapter<T> {
	private static final String TAG = ReflectiveTypeAdapter.class.getName();
	
	private final ObjectConstructor<T> constructor;
	private final Map<String, ReflectiveTypeAdapterFactory.BoundField> boundFields;
	
	ReflectiveTypeAdapter(ObjectConstructor<T> constructor,
	                      Map<String, ReflectiveTypeAdapterFactory.BoundField> boundFields) {
		this.constructor = constructor;
		this.boundFields = boundFields;
	}
	
	@Override
	public T read(JsonReader jsonReader) throws JsonException {
		JsonToken jsonToken = jsonReader.next();
		if (jsonToken == JsonToken.BEGIN_OBJECT) {
			T instance = constructor.construct();
			jsonReader.beginObject();
			while (jsonReader.hasNext()) {
				String name = jsonReader.nextName();
				ReflectiveTypeAdapterFactory.BoundField boundField = boundFields.get(name);
				if (boundField == null || !boundField.readField()) {
					jsonReader.skipValue();
				} else {
					boundField.read(jsonReader, instance);
				}
			}
			jsonReader.endObject();
			return instance;
		}
		
		JsonLog.w(TAG, "Expected a BEGIN_OBJECT, but was " + jsonToken
				+ ", skipValue " + jsonReader.getCurrentPath());
		jsonReader.skipValue();
		
		return null;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, T value) throws JsonException {
		if (value == null) {
			jsonWriter.valueNull();
			return;
		}
		
		jsonWriter.beginObject();
			for (ReflectiveTypeAdapterFactory.BoundField boundField : boundFields.values()) {
				if (boundField.writeField(value)) {
					jsonWriter.name(boundField.fieldName);
					boundField.write(jsonWriter, value);
				}
			}
		jsonWriter.endObject();
	}
}
