package com.kycq.library.json.adapter.basic;

import com.kycq.library.json.JsonException;
import com.kycq.library.json.adapter.TypeAdapter;
import com.kycq.library.json.adapter.TypeAdapterFactory;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonToken;
import com.kycq.library.json.stream.JsonWriter;

public class BooleanAdapter extends TypeAdapter<Boolean> {
	public static final BooleanAdapter ADAPTER = new BooleanAdapter();
	public static final TypeAdapterFactory FACTORY = newFactory(boolean.class, Boolean.class, ADAPTER);
	
	@Override
	public Boolean read(JsonReader jsonReader) throws JsonException {
		JsonToken jsonToken = jsonReader.next();
		if (jsonToken == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		} else if (jsonToken == JsonToken.BOOLEAN) {
			return jsonReader.nextBoolean();
		} else if (jsonToken == JsonToken.STRING) {
			String value = jsonReader.nextString();
			if ("true".equalsIgnoreCase(value)) {
				return true;
			} else if ("false".equalsIgnoreCase(value)) {
				return false;
			}
		} else {
			jsonReader.skipValue();
		}
		throw new JsonException("Expected a BOOLEAN, but was " + jsonToken);
	}
	
	@Override
	public void write(JsonWriter jsonWriter, Boolean value) throws JsonException {
		jsonWriter.value(value);
	}
}
