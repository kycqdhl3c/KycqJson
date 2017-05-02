package com.kycq.library.json.adapter.basic;

import com.kycq.library.json.JsonException;
import com.kycq.library.json.adapter.TypeAdapter;
import com.kycq.library.json.adapter.TypeAdapterFactory;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonWriter;

public class ClassAdapter extends TypeAdapter<Class> {
	public static final ClassAdapter ADAPTER = new ClassAdapter();
	public static final TypeAdapterFactory FACTORY = newFactory(Class.class, ADAPTER);
	
	@Override
	public Class read(JsonReader jsonReader) throws JsonException {
		throw new UnsupportedOperationException(
				"Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?");
	}
	
	@Override
	public void write(JsonWriter jsonWriter, Class value) throws JsonException {
		throw new UnsupportedOperationException("Attempted to serialize java.lang.Class: "
				+ value.getName() + ". Forgot to register a type adapter?");
	}
}
