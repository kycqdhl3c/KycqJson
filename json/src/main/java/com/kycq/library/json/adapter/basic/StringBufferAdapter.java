package com.kycq.library.json.adapter.basic;

import com.kycq.library.json.JsonException;
import com.kycq.library.json.adapter.TypeAdapter;
import com.kycq.library.json.adapter.TypeAdapterFactory;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonWriter;

public class StringBufferAdapter extends TypeAdapter<StringBuffer> {
	public static final StringBufferAdapter ADAPTER = new StringBufferAdapter();
	public static final TypeAdapterFactory FACTORY = newFactory(StringBuffer.class, StringBufferAdapter.ADAPTER);
	
	@Override
	public StringBuffer read(JsonReader jsonReader) throws JsonException {
		String value = StringAdapter.ADAPTER.read(jsonReader);
		if (value == null) {
			return null;
		}
		return new StringBuffer(value);
	}
	
	@Override
	public void write(JsonWriter jsonWriter, StringBuffer value) throws JsonException {
		jsonWriter.value(value.toString());
	}
}
