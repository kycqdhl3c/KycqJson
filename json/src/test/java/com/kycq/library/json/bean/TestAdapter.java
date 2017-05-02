package com.kycq.library.json.bean;

import com.kycq.library.json.JsonException;
import com.kycq.library.json.adapter.TypeAdapter;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonWriter;

public class TestAdapter extends TypeAdapter<String> {
	
	@Override
	public String read(JsonReader jsonReader) throws JsonException {
		jsonReader.skipValue();
		return null;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, String value) throws JsonException {
		jsonWriter.valueNull();
	}
}
