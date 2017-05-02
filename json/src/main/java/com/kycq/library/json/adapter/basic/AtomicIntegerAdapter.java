package com.kycq.library.json.adapter.basic;

import com.kycq.library.json.JsonException;
import com.kycq.library.json.adapter.TypeAdapter;
import com.kycq.library.json.adapter.TypeAdapterFactory;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonToken;
import com.kycq.library.json.stream.JsonWriter;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerAdapter extends TypeAdapter<AtomicInteger> {
	public static final AtomicIntegerAdapter ADAPTER = new AtomicIntegerAdapter();
	public static final TypeAdapterFactory FACTORY = newFactory(AtomicInteger.class, ADAPTER);
	
	@Override
	public AtomicInteger read(JsonReader jsonReader) throws JsonException {
		JsonToken jsonToken = jsonReader.next();
		if (jsonToken == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		} else if (jsonToken == JsonToken.NUMBER) {
			try {
				return new AtomicInteger(jsonReader.nextInt());
			} catch (JsonException e) {
				jsonReader.skipValue();
				throw e;
			}
		} else if (jsonToken == JsonToken.STRING) {
			return new AtomicInteger(Integer.parseInt(jsonReader.nextString()));
		} else {
			jsonReader.skipValue();
		}
		throw new JsonException("Expected a NUMBER, but was " + jsonToken);
	}
	
	@Override
	public void write(JsonWriter jsonWriter, AtomicInteger value) throws JsonException {
		
	}
}
