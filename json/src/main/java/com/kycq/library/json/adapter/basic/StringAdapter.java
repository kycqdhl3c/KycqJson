package com.kycq.library.json.adapter.basic;

import com.kycq.library.json.JsonException;
import com.kycq.library.json.adapter.TypeAdapter;
import com.kycq.library.json.adapter.TypeAdapterFactory;
import com.kycq.library.json.internal.LazilyParsedNumber;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonWriter;

import java.io.StringWriter;

public class StringAdapter extends TypeAdapter<String> {
	public static final StringAdapter ADAPTER = new StringAdapter();
	public static final TypeAdapterFactory FACTORY = newFactory(String.class, ADAPTER);
	
	@Override
	public String read(JsonReader jsonReader) throws JsonException {
		switch (jsonReader.next()) {
			case NULL:
				jsonReader.nextNull();
				return null;
			case BEGIN_ARRAY: {
				StringWriter writer = new StringWriter();
				JsonWriter jsonWriter = new JsonWriter(writer, false);
				readJsonArray(jsonReader, jsonWriter);
				return writer.toString();
			}
			case BEGIN_OBJECT: {
				StringWriter writer = new StringWriter();
				JsonWriter jsonWriter = new JsonWriter(writer, false);
				readJsonObject(jsonReader, jsonWriter);
				return writer.toString();
			}
			case BOOLEAN:
				return Boolean.toString(jsonReader.nextBoolean());
			case NUMBER:
			case STRING:
				return jsonReader.nextString();
			default:
				throw new JsonException("Error jsonToken " + jsonReader.next());
		}
	}
	
	@Override
	public void write(JsonWriter jsonWriter, String value) throws JsonException {
		jsonWriter.value(value);
	}
	
	/**
	 * 读取JsonArray数据
	 *
	 * @param jsonReader JSON读取流
	 * @param jsonWriter 字符串缓存
	 * @throws JsonException 错误信息
	 */
	private void readJsonArray(JsonReader jsonReader, JsonWriter jsonWriter) throws JsonException {
		jsonReader.beginArray();
		jsonWriter.beginArray();
		
		while (jsonReader.hasNext()) {
			switch (jsonReader.next()) {
				case NULL:
					jsonReader.nextNull();
					jsonWriter.valueNull();
					break;
				case BEGIN_ARRAY:
					readJsonArray(jsonReader, jsonWriter);
					break;
				case BEGIN_OBJECT:
					readJsonObject(jsonReader, jsonWriter);
					break;
				case BOOLEAN:
					jsonWriter.value(jsonReader.nextBoolean());
					break;
				case NUMBER:
					jsonWriter.value(new LazilyParsedNumber(jsonReader.nextString()));
					break;
				case STRING:
					jsonWriter.value(jsonReader.nextString());
					break;
				default:
					throw new JsonException("Error jsonToken " + jsonReader.next());
			}
		}
		
		jsonReader.endArray();
		jsonWriter.endArray();
	}
	
	/**
	 * 读取JsonObject数据
	 *
	 * @param jsonReader JSON读取流
	 * @param jsonWriter 字符串缓存
	 * @throws JsonException 错误信息
	 */
	private void readJsonObject(JsonReader jsonReader, JsonWriter jsonWriter) throws JsonException {
		jsonReader.beginObject();
		jsonWriter.beginObject();
		
		while (jsonReader.hasNext()) {
			jsonWriter.name(jsonReader.nextName());
			switch (jsonReader.next()) {
				case NULL:
					jsonReader.nextNull();
					jsonWriter.valueNull();
					break;
				case BEGIN_ARRAY:
					readJsonArray(jsonReader, jsonWriter);
					break;
				case BEGIN_OBJECT:
					readJsonObject(jsonReader, jsonWriter);
					break;
				case BOOLEAN:
					jsonWriter.value(jsonReader.nextBoolean());
					break;
				case NUMBER:
					jsonWriter.value(new LazilyParsedNumber(jsonReader.nextString()));
					break;
				case STRING:
					jsonWriter.value(jsonReader.nextString());
					break;
				default:
					throw new JsonException("Error jsonToken " + jsonReader.next());
			}
		}
		
		jsonReader.endObject();
		jsonWriter.endObject();
	}
}
