package com.kycq.library.json.adapter.basic;

import com.kycq.library.json.JsonException;
import com.kycq.library.json.adapter.TypeAdapter;
import com.kycq.library.json.adapter.TypeAdapterFactory;
import com.kycq.library.json.internal.LazilyParsedNumber;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonToken;
import com.kycq.library.json.stream.JsonWriter;

public class NumberAdapter extends TypeAdapter<Number> {
	public static final NumberAdapter ADAPTER = new NumberAdapter();
	public static final TypeAdapterFactory FACTORY = newFactory(Number.class, ADAPTER);
	
	@Override
	public Number read(JsonReader jsonReader) throws JsonException {
		JsonToken jsonToken = jsonReader.next();
		if (jsonToken == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		} else if (jsonToken == JsonToken.NUMBER || jsonToken == JsonToken.STRING) {
			return new LazilyParsedNumber(jsonReader.nextString());
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
