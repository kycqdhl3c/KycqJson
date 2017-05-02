package com.kycq.library.json.adapter;

import com.kycq.library.json.Json;
import com.kycq.library.json.JsonElement;
import com.kycq.library.json.JsonException;
import com.kycq.library.json.reflect.TypeToken;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonTreeWriter;
import com.kycq.library.json.stream.JsonWriter;

import java.io.IOException;

public abstract class TypeAdapter<T> {
	
	/**
	 * 读取JSON数据，转换为对象
	 *
	 * @param jsonReader JSON读取流
	 * @return 对象实例
	 * @throws JsonException 错误信息
	 */
	public abstract T read(JsonReader jsonReader) throws JsonException;
	
	/**
	 * 写入JSON数据
	 *
	 * @param jsonWriter JSON写入流
	 * @param value      对象实例
	 * @throws JsonException 错误信息
	 */
	public abstract void write(JsonWriter jsonWriter, T value) throws JsonException;
	
	public final JsonElement toJsonTree(T value) {
		try {
			JsonTreeWriter jsonWriter = new JsonTreeWriter();
			write(jsonWriter, value);
			return jsonWriter.get();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static <TT> TypeAdapterFactory newFactory(final Class<TT> type,
	                                                 final TypeAdapter<TT> typeAdapter) {
		return new TypeAdapterFactory() {
			@SuppressWarnings("unchecked")
			@Override
			public <T> TypeAdapter<T> create(Json json, TypeToken<T> typeToken) {
				return typeToken.getRawType() == type ? (TypeAdapter<T>) typeAdapter : null;
			}
			
			@Override
			public String toString() {
				return "Factory[type = " + type.getName() + ", adapter = " + typeAdapter + "]";
			}
		};
	}
	
	public static <TT> TypeAdapterFactory newFactory(final Class<TT> unBoxedType,
	                                                 final Class<TT> boxedType,
	                                                 final TypeAdapter<? super TT> typeAdapter) {
		return new TypeAdapterFactory() {
			@SuppressWarnings("unchecked")
			@Override
			public <T> TypeAdapter<T> create(Json json, TypeToken<T> typeToken) {
				Class<? super T> rawType = typeToken.getRawType();
				return (rawType == unBoxedType || rawType == boxedType)
						? (TypeAdapter<T>) typeAdapter : null;
			}
			
			@Override
			public String toString() {
				return "Factory[type = " + boxedType.getName()
						+ " & " + unBoxedType.getName()
						+ ", adapter = " + typeAdapter + "]";
			}
		};
	}
}
