package com.kycq.library.json;

import com.kycq.library.json.stream.JsonReader;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class JsonArray extends JsonElement implements Iterable<JsonElement>, Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	/** 参数集合 */
	private final List<JsonElement> mElementList = new ArrayList<>(10);
	
	/**
	 * 构造方法
	 */
	public JsonArray() {
	}
	
	/**
	 * 构造方法
	 *
	 * @param elementList 参数集合
	 */
	public JsonArray(List<JsonElement> elementList) {
		mElementList.addAll(elementList);
	}
	
	/**
	 * 构造方法
	 *
	 * @param jsonStr json字符串
	 * @throws IOException 错误信息
	 */
	public JsonArray(String jsonStr) throws IOException {
		this(new Json(), jsonStr);
	}
	
	/**
	 * 构造方法
	 *
	 * @param json    JSON对象
	 * @param jsonStr json字符串
	 * @throws IOException 错误信息
	 */
	public JsonArray(Json json, String jsonStr) throws IOException {
		JsonArray jsonArray = json.fromJson(jsonStr, JsonArray.class);
		mElementList.addAll(jsonArray.mElementList);
	}
	
	/**
	 * 构造方法
	 *
	 * @param reader JSON数据读取流
	 * @throws IOException 错误信息
	 */
	public JsonArray(Reader reader) throws IOException {
		this(new Json(), reader);
	}
	
	/**
	 * 构造方法
	 *
	 * @param json   JSON对象
	 * @param reader JSON数据读取流
	 * @throws IOException 错误信息
	 */
	public JsonArray(Json json, Reader reader) throws IOException {
		JsonArray jsonArray = json.fromJson(reader, JsonArray.class);
		mElementList.addAll(jsonArray.mElementList);
	}
	
	/**
	 * 构造方法
	 *
	 * @param jsonReader JSON读取流
	 * @throws IOException 错误信息
	 */
	public JsonArray(JsonReader jsonReader) throws IOException {
		this(new Json(), jsonReader);
	}
	
	/**
	 * 构造方法
	 *
	 * @param json       JSON对象
	 * @param jsonReader JSON读取流
	 * @throws IOException 错误信息
	 */
	public JsonArray(Json json, JsonReader jsonReader) throws IOException {
		JsonArray jsonArray = json.fromJson(jsonReader, JsonArray.class);
		mElementList.addAll(jsonArray.mElementList);
	}
	
	/**
	 * 获取数组长度
	 *
	 * @return 数组长度
	 */
	public int length() {
		return mElementList.size();
	}
	
	/**
	 * 获取指定位置的值
	 *
	 * @param index 指定位置
	 * @return 对象值
	 */
	public JsonElement get(int index) {
		return mElementList.get(index);
	}
	
	/**
	 * 获取指定位置的JsonArray值
	 *
	 * @param index 指定位置
	 * @return JsonArray对象值
	 * @throws IOException 错误信息
	 */
	public JsonArray getJsonArray(int index) throws IOException {
		JsonElement value = get(index);
		
		if (value instanceof JsonArray) {
			return (JsonArray) value;
		}
		
		throw new IOException("Value " + value + " at a of type " + value.getClass().getName() + " cannot be converted to JsonArray");
	}
	
	/**
	 * 获取指定位置的JsonObject值
	 *
	 * @param index 指定位置
	 * @return JsonObject对象值
	 * @throws IOException 错误信息
	 */
	public JsonObject getJsonObject(int index) throws IOException {
		JsonElement value = get(index);
		
		if (value instanceof JsonObject) {
			return (JsonObject) value;
		}
		
		throw new IOException("Value " + value + " at a of type " + value.getClass().getName() + " cannot be converted to JsonObject");
	}
	
	/**
	 * 获取指定位置的Boolean值
	 *
	 * @param index 指定位置
	 * @return Boolean值
	 * @throws IOException 错误信息
	 */
	public boolean getBoolean(int index) throws IOException {
		JsonElement value = get(index);
		
		if (value instanceof JsonPrimitive) {
			if (((JsonPrimitive) value).isBoolean()) {
				return value.getAsBoolean();
			} else if (((JsonPrimitive) value).isString()) {
				if ("true".equalsIgnoreCase(value.getAsString())) {
					return true;
				} else if ("false".equalsIgnoreCase(value.getAsString())) {
					return false;
				}
			}
		}
		
		throw new IOException("Value " + value + " at a of type " + value.getClass().getName() + " cannot be converted to boolean");
	}
	
	/**
	 * 获取指定位置的Double值
	 *
	 * @param index 指定位置
	 * @return Double值
	 * @throws IOException 错误信息
	 */
	public double getDouble(int index) throws IOException {
		JsonElement value = get(index);
		
		if (value instanceof JsonPrimitive) {
			try {
				if (((JsonPrimitive) value).isNumber()
						|| ((JsonPrimitive) value).isString()) {
					return value.getAsDouble();
				}
			} catch (Exception ignored) {
				throw new IOException(ignored);
			}
		}
		
		throw new IOException("Value " + value + " at a of type " + value.getClass().getName() + " cannot be converted to double");
	}
	
	/**
	 * 获取指定位置的Float值
	 *
	 * @param index 指定位置
	 * @return Float值
	 * @throws IOException 错误信息
	 */
	public float getFloat(int index) throws IOException {
		JsonElement value = get(index);
		
		if (value instanceof JsonPrimitive) {
			try {
				if (((JsonPrimitive) value).isNumber()
						|| ((JsonPrimitive) value).isString()) {
					return value.getAsFloat();
				}
			} catch (Exception ignored) {
				throw new IOException(ignored);
			}
		}
		
		throw new IOException("Value " + value + " at a of type " + value.getClass().getName() + " cannot be converted to float");
	}
	
	/**
	 * 获取指定位置的Long值
	 *
	 * @param index 指定位置
	 * @return Long值
	 * @throws IOException 错误信息
	 */
	public long getLong(int index) throws IOException {
		JsonElement value = get(index);
		
		if (value instanceof JsonPrimitive) {
			try {
				if (((JsonPrimitive) value).isNumber()
						|| ((JsonPrimitive) value).isString()) {
					return value.getAsLong();
				}
			} catch (Exception ignored) {
				throw new IOException(ignored);
			}
		}
		
		throw new IOException("Value " + value + " at a of type " + value.getClass().getName() + " cannot be converted to long");
	}
	
	/**
	 * 获取指定位置的Integer值
	 *
	 * @param index 指定位置
	 * @return Integer值
	 * @throws IOException 错误信息
	 */
	public int getInt(int index) throws IOException {
		JsonElement value = get(index);
		
		if (value instanceof JsonPrimitive) {
			try {
				if (((JsonPrimitive) value).isNumber()
						|| ((JsonPrimitive) value).isString()) {
					return value.getAsInt();
				}
			} catch (Exception ignored) {
				throw new IOException(ignored);
			}
		}
		
		throw new IOException("Value " + value + " at a of type " + value.getClass().getName() + " cannot be converted to int");
	}
	
	/**
	 * 获取指定位置的String值
	 *
	 * @param index 指定位置
	 * @return String值
	 */
	public String getString(int index) {
		JsonElement value = get(index);
		
		if (value instanceof JsonPrimitive) {
			return value.getAsString();
		}
		
		return String.valueOf(value);
	}
	
	/**
	 * 获取指定位置的对象值
	 *
	 * @param index 指定位置
	 * @return 对象值
	 */
	public JsonElement opt(int index) {
		return get(index);
	}
	
	/**
	 * 获取指定位置的JsonArray值
	 *
	 * @param index 指定位置
	 * @return JsonArray对象值
	 */
	public JsonArray optJsonArray(int index) {
		try {
			return getJsonArray(index);
		} catch (IOException ignored) {
			return null;
		}
	}
	
	/**
	 * 获取指定位置的JsonArray值
	 *
	 * @param index    指定位置
	 * @param fallback 默认值
	 * @return JsonArray对象值
	 */
	public JsonArray optJsonArray(int index, JsonArray fallback) {
		try {
			return getJsonArray(index);
		} catch (IOException ignored) {
			return fallback;
		}
	}
	
	/**
	 * 获取指定位置的JsonObject值
	 *
	 * @param index 指定位置
	 * @return JsonObject对象值
	 */
	public JsonObject optJsonObject(int index) {
		try {
			return getJsonObject(index);
		} catch (IOException ignored) {
			return null;
		}
	}
	
	/**
	 * 获取指定位置的JsonObject值
	 *
	 * @param index    指定位置
	 * @param fallback 默认值
	 * @return JsonObject对象值
	 */
	public JsonObject optJsonObject(int index, JsonObject fallback) {
		try {
			return getJsonObject(index);
		} catch (IOException ignored) {
			return fallback;
		}
	}
	
	/**
	 * 获取指定位置的Boolean值
	 *
	 * @param index 指定位置
	 * @return Boolean值
	 */
	public boolean optBoolean(int index) {
		try {
			return getBoolean(index);
		} catch (IOException ignored) {
			return false;
		}
	}
	
	/**
	 * 获取指定位置的Boolean值
	 *
	 * @param index    指定位置
	 * @param fallback 默认值
	 * @return Boolean值
	 */
	public boolean optBoolean(int index, boolean fallback) {
		try {
			return getBoolean(index);
		} catch (IOException ignored) {
			return fallback;
		}
	}
	
	/**
	 * 获取指定位置的Double值
	 *
	 * @param index 指定位置
	 * @return Double值
	 */
	public double optDouble(int index) {
		try {
			return getDouble(index);
		} catch (IOException ignored) {
			return 0D;
		}
	}
	
	/**
	 * 获取指定位置的Double值
	 *
	 * @param index    指定位置
	 * @param fallback 默认值
	 * @return Double值
	 */
	public double optDouble(int index, double fallback) {
		try {
			return getDouble(index);
		} catch (IOException ignored) {
			return fallback;
		}
	}
	
	/**
	 * 获取指定位置的Float值
	 *
	 * @param index 指定位置
	 * @return Float值
	 */
	public double optFloat(int index) {
		try {
			return getFloat(index);
		} catch (IOException ignored) {
			return 0D;
		}
	}
	
	/**
	 * 获取指定位置的Float值
	 *
	 * @param index    指定位置
	 * @param fallback 默认值
	 * @return Float值
	 */
	public float optFloat(int index, float fallback) {
		try {
			return getFloat(index);
		} catch (IOException ignored) {
			return fallback;
		}
	}
	
	/**
	 * 获取指定位置的Long值
	 *
	 * @param index 指定位置
	 * @return Long值
	 */
	public long optLong(int index) {
		try {
			return getLong(index);
		} catch (IOException ignored) {
			return 0L;
		}
	}
	
	/**
	 * 获取指定位置的Long值
	 *
	 * @param index    指定位置
	 * @param fallback 默认值
	 * @return Long值
	 */
	public long optLong(int index, long fallback) {
		try {
			return getLong(index);
		} catch (IOException ignored) {
			return fallback;
		}
	}
	
	/**
	 * 获取指定位置的Integer值
	 *
	 * @param index 指定位置
	 * @return Integer值
	 */
	public int optInt(int index) {
		try {
			return getInt(index);
		} catch (IOException ignored) {
			return 0;
		}
	}
	
	/**
	 * 获取指定位置的Integer值
	 *
	 * @param index    指定位置
	 * @param fallback 默认值
	 * @return Integer值
	 */
	public int optInt(int index, int fallback) {
		try {
			return getInt(index);
		} catch (IOException ignored) {
			return fallback;
		}
	}
	
	/**
	 * 获取指定位置的String值
	 *
	 * @param index 指定位置
	 * @return String值
	 */
	public String optString(int index) {
		return getString(index);
	}
	
	/**
	 * 获取指定位置的String值
	 *
	 * @param index    指定位置
	 * @param fallback 默认值
	 * @return String值
	 */
	public String optString(int index, String fallback) {
		String value = getString(index);
		
		if (value != null) {
			return value;
		}
		
		return fallback;
	}
	
	/**
	 * 存储对象值
	 *
	 * @param value 对象值
	 */
	public void put(JsonElement value) {
		mElementList.add(value);
	}
	
	/**
	 * 存储指定位置的对象值
	 *
	 * @param index 指定位置
	 * @param value 对象值
	 */
	public void put(int index, JsonElement value) {
		mElementList.add(index, value);
	}
	
	/**
	 * 存储JsonArray对象
	 *
	 * @param value JsonArray对象值
	 */
	public void put(JsonArray value) {
		mElementList.add(value);
	}
	
	/**
	 * 存储JsonObject对象
	 *
	 * @param index 指定位置
	 * @param value JsonObject对象值
	 */
	public void put(int index, JsonArray value) {
		mElementList.add(index, value);
	}
	
	/**
	 * 存储JsonObject对象
	 *
	 * @param value JsonObject对象值
	 */
	public void put(JsonObject value) {
		mElementList.add(value);
	}
	
	/**
	 * 存储指定位置的JsonObject对象
	 *
	 * @param index 指定位置
	 * @param value JsonObject对象值
	 */
	public void put(int index, JsonObject value) {
		mElementList.add(index, value);
	}
	
	/**
	 * 存储Boolean值
	 *
	 * @param value Boolean值
	 */
	public void put(boolean value) {
		mElementList.add(new JsonPrimitive(value));
	}
	
	/**
	 * 存储指定位置的Boolean值
	 *
	 * @param index 指定位置
	 * @param value Boolean值
	 */
	public void put(int index, boolean value) {
		mElementList.add(index, new JsonPrimitive(value));
	}
	
	/**
	 * 存储Double值
	 *
	 * @param value Double值
	 */
	public void put(double value) {
		mElementList.add(new JsonPrimitive(value));
	}
	
	/**
	 * 存储指定位置的Double值
	 *
	 * @param index 指定位置
	 * @param value Double值
	 */
	public void put(int index, double value) {
		mElementList.add(index, new JsonPrimitive(value));
	}
	
	/**
	 * 存储Long值
	 *
	 * @param value Long值
	 */
	public void put(long value) {
		mElementList.add(new JsonPrimitive(value));
	}
	
	/**
	 * 存储指定位置的Long值
	 *
	 * @param index 指定位置
	 * @param value Long值
	 */
	public void put(int index, long value) {
		mElementList.add(index, new JsonPrimitive(value));
	}
	
	/**
	 * 存储Integer值
	 *
	 * @param value Integer值
	 */
	public void put(int value) {
		mElementList.add(new JsonPrimitive(value));
	}
	
	/**
	 * 存储指定位置的Integer值
	 *
	 * @param index 指定位置
	 * @param value Integer值
	 */
	public void put(int index, int value) {
		mElementList.add(index, new JsonPrimitive(value));
	}
	
	/**
	 * 存储String值
	 *
	 * @param value String值
	 */
	public void put(String value) {
		if (value == null) {
			mElementList.add(JsonNull.INSTANCE);
		} else {
			mElementList.add(new JsonPrimitive(value));
		}
	}
	
	/**
	 * 存储指定位置的String值
	 *
	 * @param index 指定位置
	 * @param value String值
	 */
	public void put(int index, String value) {
		if (value == null) {
			mElementList.add(index, JsonNull.INSTANCE);
		} else {
			mElementList.add(index, new JsonPrimitive(value));
		}
	}
	
	@Override
	public Iterator<JsonElement> iterator() {
		return mElementList.iterator();
	}
	
	/**
	 * 删除指定位置的值
	 *
	 * @param index 指定位置
	 * @return 删除值
	 */
	public JsonElement remove(int index) {
		return mElementList.remove(index);
	}
	
	/**
	 * 删除指定值
	 *
	 * @param value 删除值
	 */
	public void remove(JsonElement value) {
		mElementList.remove(value);
	}
	
	/**
	 * 批量存储值
	 *
	 * @param collection 批量值
	 */
	public void putAll(Collection<JsonElement> collection) {
		mElementList.addAll(collection);
	}
	
	/**
	 * 清空所有值
	 */
	public void clear() {
		mElementList.clear();
	}
	
	@Override
	protected JsonArray clone() throws CloneNotSupportedException {
		return (JsonArray) super.clone();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof JsonArray && ((JsonArray) obj).mElementList.equals(mElementList);
	}
	
	@Override
	public int hashCode() {
		return mElementList.hashCode();
	}
	
}
