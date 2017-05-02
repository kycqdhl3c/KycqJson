package com.kycq.library.json.adapter.basic;

import com.kycq.library.json.JsonException;
import com.kycq.library.json.adapter.TypeAdapter;
import com.kycq.library.json.adapter.TypeAdapterFactory;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonWriter;

public class StringBuilderAdapter extends TypeAdapter<StringBuilder> {
	public static final StringBuilderAdapter ADAPTER = new StringBuilderAdapter();
	public static final TypeAdapterFactory FACTORY = newFactory(StringBuilder.class, StringBuilderAdapter.ADAPTER);
	
	@Override
	public StringBuilder read(JsonReader jsonReader) throws JsonException {
		String value = StringAdapter.ADAPTER.read(jsonReader);
		if (value == null) {
			return null;
		}
		return new StringBuilder(value);
	}
	
	@Override
	public void write(JsonWriter jsonWriter, StringBuilder value) throws JsonException {
		jsonWriter.value(value.toString());
	}
}
