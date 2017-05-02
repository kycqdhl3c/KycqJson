package com.kycq.library.json.adapter.basic;

import com.kycq.library.json.JsonException;
import com.kycq.library.json.adapter.TypeAdapter;
import com.kycq.library.json.adapter.TypeAdapterFactory;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonToken;
import com.kycq.library.json.stream.JsonWriter;

import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicBooleanAdapter extends TypeAdapter<AtomicBoolean> {
	public static final AtomicBooleanAdapter ADAPTER = new AtomicBooleanAdapter();
	public static final TypeAdapterFactory FACTORY = newFactory(AtomicBoolean.class, ADAPTER);
	
	@Override
	public AtomicBoolean read(JsonReader jsonReader) throws JsonException {
		JsonToken jsonToken = jsonReader.next();
		if (jsonToken == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		} else if (jsonToken == JsonToken.BOOLEAN) {
			return new AtomicBoolean(jsonReader.nextBoolean());
		} else if (jsonToken == JsonToken.STRING) {
			String value = jsonReader.nextString();
			if ("true".equalsIgnoreCase(value)) {
				return new AtomicBoolean(true);
			} else if ("false".equalsIgnoreCase(value)) {
				return new AtomicBoolean(false);
			}
		} else {
			jsonReader.skipValue();
		}
		throw new JsonException("Expected a BOOLEAN, but was " + jsonToken);
	}
	
	@Override
	public void write(JsonWriter jsonWriter, AtomicBoolean value) throws JsonException {
		jsonWriter.value(value.get());
	}
}
