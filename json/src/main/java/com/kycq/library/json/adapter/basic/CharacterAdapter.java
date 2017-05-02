package com.kycq.library.json.adapter.basic;

import com.kycq.library.json.JsonException;
import com.kycq.library.json.adapter.TypeAdapter;
import com.kycq.library.json.adapter.TypeAdapterFactory;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonToken;
import com.kycq.library.json.stream.JsonWriter;

public class CharacterAdapter extends TypeAdapter<Character> {
	public static final CharacterAdapter ADAPTER = new CharacterAdapter();
	public static final TypeAdapterFactory FACTORY = newFactory(char.class, Character.class, ADAPTER);
	
	@Override
	public Character read(JsonReader jsonReader) throws JsonException {
		JsonToken jsonToken = jsonReader.next();
		if (jsonToken == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		} else if (jsonToken == JsonToken.STRING) {
			String str = jsonReader.nextString();
			if (str.length() == 1) {
				return str.charAt(0);
			}
		} else {
			jsonReader.skipValue();
		}
		throw new JsonException("Expected a STRING, but was " + jsonToken);
	}
	
	@Override
	public void write(JsonWriter jsonWriter, Character value) throws JsonException {
		jsonWriter.value(value == null ? null : String.valueOf(value));
	}
}
