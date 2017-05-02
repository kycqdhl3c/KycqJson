package com.kycq.library.json.adapter.basic;

import com.kycq.library.json.JsonException;
import com.kycq.library.json.adapter.TypeAdapter;
import com.kycq.library.json.adapter.TypeAdapterFactory;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonToken;
import com.kycq.library.json.stream.JsonWriter;

public class ShortAdapter extends TypeAdapter<Number> {
	public static final ShortAdapter ADAPTER = new ShortAdapter();
	public static final TypeAdapterFactory FACTORY = newFactory(short.class, Short.class, ADAPTER);
	
	@Override
	public Number read(JsonReader jsonReader) throws JsonException {
		JsonToken jsonToken = jsonReader.next();
		if (jsonToken == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		} else if (jsonToken == JsonToken.NUMBER) {
			try {
				return (short) jsonReader.nextInt();
			} catch (JsonException e) {
				jsonReader.skipValue();
				throw e;
			}
		} else if (jsonToken == JsonToken.STRING) {
			return Short.parseShort(jsonReader.nextString());
		} else {
			jsonReader.skipValue();
		}
		throw new JsonException("Expected a NUMBER, but was " + jsonToken);
	}
	
	@Override
	public void write(JsonWriter jsonWriter, Number value) throws JsonException {
		
	}
}
