package com.kycq.library.json.adapter.basic;

import com.kycq.library.json.JsonException;
import com.kycq.library.json.adapter.TypeAdapter;
import com.kycq.library.json.adapter.TypeAdapterFactory;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonToken;
import com.kycq.library.json.stream.JsonWriter;

import java.math.BigDecimal;

public class BigDecimalAdapter extends TypeAdapter<BigDecimal> {
	public static final BigDecimalAdapter ADAPTER = new BigDecimalAdapter();
	public static final TypeAdapterFactory FACTORY = newFactory(BigDecimal.class, ADAPTER);
	
	@Override
	public BigDecimal read(JsonReader jsonReader) throws JsonException {
		JsonToken jsonToken = jsonReader.next();
		if (jsonToken == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		} else if (jsonToken == JsonToken.NUMBER || jsonToken == JsonToken.STRING) {
			return new BigDecimal(jsonReader.nextString());
		} else {
			jsonReader.skipValue();
		}
		throw new JsonException("Expected a STRING, but was " + jsonToken);
	}
	
	@Override
	public void write(JsonWriter jsonWriter, BigDecimal value) throws JsonException {
		jsonWriter.value(value);
	}
}
