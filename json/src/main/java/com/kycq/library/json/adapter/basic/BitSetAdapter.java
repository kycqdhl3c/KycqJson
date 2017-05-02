package com.kycq.library.json.adapter.basic;

import com.kycq.library.json.JsonException;
import com.kycq.library.json.adapter.TypeAdapter;
import com.kycq.library.json.adapter.TypeAdapterFactory;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonToken;
import com.kycq.library.json.stream.JsonWriter;

import java.util.BitSet;

public class BitSetAdapter extends TypeAdapter<BitSet> {
	public static final BitSetAdapter ADAPTER = new BitSetAdapter();
	public static final TypeAdapterFactory FACTORY = newFactory(BitSet.class, ADAPTER);
	
	@Override
	public BitSet read(JsonReader jsonReader) throws JsonException {
		JsonToken jsonToken = jsonReader.next();
		if (jsonToken == JsonToken.BEGIN_ARRAY) {
			jsonReader.beginArray();
			BitSet bitSet = new BitSet();
			int index = 0;
			JsonException jsonException = null;
			while (jsonReader.hasNext()) {
				if (jsonException != null) {
					jsonReader.skipValue();
					continue;
				}
				boolean set = false;
				switch (jsonReader.next()) {
					case BOOLEAN:
						set = jsonReader.nextBoolean();
						break;
					case NUMBER:
					case STRING:
						try {
							set = Integer.parseInt(jsonReader.nextString()) != 0;
						} catch (Exception e) {
							jsonException = new JsonException(e);
						}
						break;
					default:
						jsonException = new JsonException("");
				}
				if (set) {
					bitSet.set(index);
				}
				index++;
			}
			jsonReader.endArray();
			if (jsonException != null) {
				throw jsonException;
			}
			return bitSet;
		}
		throw new JsonException("Expected a BEGIN_ARRAY, but was " + jsonToken);
	}
	
	@Override
	public void write(JsonWriter jsonWriter, BitSet value) throws JsonException {
		jsonWriter.beginArray();
		for (int index = 0, length = value.length(); index < length; index++) {
			jsonWriter.value((value.get(index)) ? 1 : 0);
		}
		jsonWriter.endArray();
	}
}
