package com.kycq.library.json.adapter;

import com.kycq.library.json.JsonArray;
import com.kycq.library.json.JsonElement;
import com.kycq.library.json.JsonException;
import com.kycq.library.json.JsonNull;
import com.kycq.library.json.JsonObject;
import com.kycq.library.json.JsonPrimitive;
import com.kycq.library.json.internal.LazilyParsedNumber;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonWriter;

import java.util.Map;

public class JsonElementAdapter extends TypeAdapter<JsonElement> {
	public static final JsonElementAdapter ADAPTER = new JsonElementAdapter();
	
	@Override
	public JsonElement read(JsonReader jsonReader) throws JsonException {
		switch (jsonReader.next()) {
			case STRING:
				return new JsonPrimitive(jsonReader.nextString());
			case NUMBER:
				String number = jsonReader.nextString();
				return new JsonPrimitive(new LazilyParsedNumber(number));
			case BOOLEAN:
				return new JsonPrimitive(jsonReader.nextBoolean());
			case NULL:
				jsonReader.nextNull();
				return JsonNull.INSTANCE;
			case BEGIN_ARRAY:
				JsonArray array = new JsonArray();
				jsonReader.beginArray();
				while (jsonReader.hasNext()) {
					array.put(read(jsonReader));
				}
				jsonReader.endArray();
				return array;
			case BEGIN_OBJECT:
				JsonObject object = new JsonObject();
				jsonReader.beginObject();
				while (jsonReader.hasNext()) {
					object.put(jsonReader.nextName(), read(jsonReader));
				}
				jsonReader.endObject();
				return object;
			case END_DOCUMENT:
			case NAME:
			case END_OBJECT:
			case END_ARRAY:
			default:
				throw new IllegalArgumentException();
		}
	}
	
	@Override
	public void write(JsonWriter jsonWriter, JsonElement value) throws JsonException {
		if (value == null || value.isJsonNull()) {
			jsonWriter.valueNull();
		} else if (value.isJsonPrimitive()) {
			JsonPrimitive primitive = value.getAsJsonPrimitive();
			if (primitive.isNumber()) {
				jsonWriter.value(primitive.getAsNumber());
			} else if (primitive.isBoolean()) {
				jsonWriter.value(primitive.getAsBoolean());
			} else {
				jsonWriter.value(primitive.getAsString());
			}
		} else if (value.isJsonArray()) {
			jsonWriter.beginArray();
			for (JsonElement jsonElement : value.getAsJsonArray()) {
				write(jsonWriter, jsonElement);
			}
			jsonWriter.endArray();
		} else if (value.isJsonObject()) {
			jsonWriter.beginObject();
			for (Map.Entry<String, JsonElement> entry : value.getAsJsonObject().entrySet()) {
				jsonWriter.name(entry.getKey());
				write(jsonWriter, entry.getValue());
			}
			jsonWriter.endObject();
		} else {
			throw new IllegalArgumentException("Couldn't write " + value.getClass());
		}
	}
}
