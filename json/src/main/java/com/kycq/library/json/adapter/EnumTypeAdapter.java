package com.kycq.library.json.adapter;

import com.kycq.library.json.JsonException;
import com.kycq.library.json.annotation.JsonName;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonToken;
import com.kycq.library.json.stream.JsonWriter;

import java.util.HashMap;
import java.util.Map;

public class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {
	private final Map<String, T> nameToConstant = new HashMap<>();
	private final Map<T, String> constantToName = new HashMap<>();
	
	public EnumTypeAdapter(Class<T> clazz) {
		try {
			for (T constant : clazz.getEnumConstants()) {
				String name = constant.name();
				JsonName annotation = clazz.getField(name).getAnnotation(JsonName.class);
				if (annotation != null) {
					name = annotation.value();
					for (String alternate : annotation.alternate()) {
						nameToConstant.put(alternate, constant);
					}
				}
				nameToConstant.put(name, constant);
				constantToName.put(constant, name);
			}
		} catch (NoSuchFieldException e) {
			throw new AssertionError(e);
		}
	}
	
	@Override
	public T read(JsonReader jsonReader) throws JsonException {
		JsonToken jsonToken = jsonReader.next();
		if (jsonToken == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		} else if (jsonToken == JsonToken.STRING) {
			return nameToConstant.get(jsonReader.nextString());
		}
		throw new JsonException("Expected a STRING, but was " + jsonToken);
	}
	
	@Override
	public void write(JsonWriter jsonWriter, T value) throws JsonException {
		jsonWriter.value(value == null ? null : constantToName.get(value));
	}
}
