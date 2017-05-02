package com.kycq.library.json.adapter.basic;

import com.kycq.library.json.JsonException;
import com.kycq.library.json.adapter.TypeAdapter;
import com.kycq.library.json.adapter.TypeAdapterFactory;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonToken;
import com.kycq.library.json.stream.JsonWriter;

public class ByteAdapter extends TypeAdapter<Number> {
	public static final ByteAdapter ADAPTER = new ByteAdapter();
	public static final TypeAdapterFactory FACTORY = newFactory(byte.class, Byte.class, ADAPTER);
	
	@Override
	public Number read(JsonReader jsonReader) throws JsonException {
		JsonToken jsonToken = jsonReader.next();
		if (jsonToken == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		} else if (jsonToken == JsonToken.NUMBER) {
			try {
				return (byte) jsonReader.nextInt();
			} catch (JsonException e) {
				jsonReader.skipValue();
				throw e;
			}
		} else if (jsonToken == JsonToken.STRING) {
			return Byte.parseByte(jsonReader.nextString());
		} else {
			jsonReader.skipValue();
		}
		throw new JsonException("Expected a NUMBER, but was " + jsonToken);
	}
	
	@Override
	public void write(JsonWriter jsonWriter, Number value) throws JsonException {
		jsonWriter.value(value);
	}
}
