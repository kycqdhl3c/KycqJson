package com.kycq.library.json.adapter.basic;

import com.kycq.library.json.JsonException;
import com.kycq.library.json.adapter.TypeAdapter;
import com.kycq.library.json.adapter.TypeAdapterFactory;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonToken;
import com.kycq.library.json.stream.JsonWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicIntegerArrayAdapter extends TypeAdapter<AtomicIntegerArray> {
	public static final AtomicIntegerArrayAdapter ADAPTER = new AtomicIntegerArrayAdapter();
	public static final TypeAdapterFactory FACTORY = newFactory(AtomicIntegerArray.class, ADAPTER);
	
	@Override
	public AtomicIntegerArray read(JsonReader jsonReader) throws JsonException {
		JsonToken jsonToken = jsonReader.next();
		if (jsonToken == JsonToken.BEGIN_ARRAY) {
			JsonException jsonException = null;
			List<Integer> list = new ArrayList<>();
			jsonReader.beginArray();
			while (jsonReader.hasNext()) {
				if (jsonException != null) {
					jsonReader.skipValue();
					continue;
				}
				try {
					int integer = jsonReader.nextInt();
					list.add(integer);
				} catch (JsonException e) {
					jsonException = e;
				}
			}
			jsonReader.endArray();
			if (jsonException != null) {
				throw jsonException;
			}
			
			int length = list.size();
			AtomicIntegerArray array = new AtomicIntegerArray(length);
			for (int index = 0; index < length; ++index) {
				array.set(index, list.get(index));
			}
			return array;
		}
		
		jsonReader.skipValue();
		
		return null;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, AtomicIntegerArray value) throws JsonException {
		jsonWriter.beginArray();
		for (int index = 0, length = value.length(); index < length; index++) {
			jsonWriter.value(value.get(index));
		}
		jsonWriter.endArray();
	}
}
