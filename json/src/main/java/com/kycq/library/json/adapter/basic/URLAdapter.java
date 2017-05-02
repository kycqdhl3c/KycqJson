package com.kycq.library.json.adapter.basic;

import com.kycq.library.json.JsonException;
import com.kycq.library.json.adapter.TypeAdapter;
import com.kycq.library.json.adapter.TypeAdapterFactory;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonToken;
import com.kycq.library.json.stream.JsonWriter;

import java.net.MalformedURLException;
import java.net.URL;

public class URLAdapter extends TypeAdapter<URL> {
	public static final URLAdapter ADAPTER = new URLAdapter();
	public static final TypeAdapterFactory FACTORY = newFactory(URL.class, ADAPTER);
	
	@Override
	public URL read(JsonReader jsonReader) throws JsonException {
		JsonToken jsonToken = jsonReader.next();
		if (jsonToken == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		} else if (jsonToken == JsonToken.STRING) {
			try {
				return new URL(jsonReader.nextString());
			} catch (MalformedURLException e) {
				throw new JsonException(e);
			}
		} else {
			jsonReader.skipValue();
		}
		throw new JsonException("Expected a STRING, but was " + jsonToken);
	}
	
	@Override
	public void write(JsonWriter jsonWriter, URL value) throws JsonException {
		
	}
}
