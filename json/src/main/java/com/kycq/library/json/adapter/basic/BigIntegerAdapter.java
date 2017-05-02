package com.kycq.library.json.adapter.basic;

import com.kycq.library.json.JsonException;
import com.kycq.library.json.adapter.TypeAdapter;
import com.kycq.library.json.adapter.TypeAdapterFactory;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonToken;
import com.kycq.library.json.stream.JsonWriter;

import java.math.BigInteger;

public class BigIntegerAdapter extends TypeAdapter<BigInteger> {
	public static final BigIntegerAdapter ADAPTER = new BigIntegerAdapter();
	public static final TypeAdapterFactory FACTORY = newFactory(BigInteger.class, ADAPTER);
	
	@Override
	public BigInteger read(JsonReader jsonReader) throws JsonException {
		JsonToken jsonToken = jsonReader.next();
		if (jsonToken == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		} else if (jsonToken == JsonToken.NUMBER || jsonToken == JsonToken.STRING) {
			return new BigInteger(jsonReader.nextString());
		} else {
			jsonReader.skipValue();
		}
		throw new JsonException("Expected a STRING, but was " + jsonToken);
	}
	
	@Override
	public void write(JsonWriter jsonWriter, BigInteger value) throws JsonException {
		jsonWriter.value(value);
	}
}
