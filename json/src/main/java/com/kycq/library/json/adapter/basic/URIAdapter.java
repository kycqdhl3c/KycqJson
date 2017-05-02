package com.kycq.library.json.adapter.basic;

import com.kycq.library.json.JsonException;
import com.kycq.library.json.adapter.TypeAdapter;
import com.kycq.library.json.adapter.TypeAdapterFactory;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonToken;
import com.kycq.library.json.stream.JsonWriter;

import java.net.URI;
import java.net.URISyntaxException;

public class URIAdapter extends TypeAdapter<URI> {
	public static final URIAdapter ADAPTER = new URIAdapter();
	public static final TypeAdapterFactory FACTORY = newFactory(URI.class, ADAPTER);
	
	@Override
	public URI read(JsonReader jsonReader) throws JsonException {
		JsonToken jsonToken = jsonReader.next();
		if (jsonToken == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		} else if (jsonToken == JsonToken.STRING) {
			try {
				return new URI(jsonReader.nextString());
			} catch (URISyntaxException e) {
				throw new JsonException(e);
			}
		} else {
			jsonReader.skipValue();
		}
		throw new JsonException("Expected a STRING, but was " + jsonToken);
	}
	
	@Override
	public void write(JsonWriter jsonWriter, URI value) throws JsonException {
		jsonWriter.value(value == null ? null : value.toASCIIString());
	}
}
