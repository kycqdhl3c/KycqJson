package com.kycq.library.json;

import com.kycq.library.json.internal.LinkedTreeMap;
import com.kycq.library.json.stream.JsonReader;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class JsonObject extends JsonElement implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	/** 参数集合 */
	private final Map<String, JsonElement> mElementMap = new LinkedTreeMap<>();
	
	/**
	 * 构造方法
	 */
	public JsonObject() {
	}
	
	/**
	 * 构造方法
	 *
	 * @param map 参数集合
	 */
	public JsonObject(Map<String, JsonElement> map) {
		mElementMap.putAll(map);
	}
	
	/**
	 * 构造方法
	 *
	 * @param json    JSON对象
	 * @param jsonStr json字符串
	 * @throws IOException 错误信息
	 */
	public JsonObject(Json json, String jsonStr) throws IOException {
		JsonObject jsonObject = json.fromJson(jsonStr, JsonObject.class);
		if (jsonObject == null) {
			throw new IOException("json is wrong format");
		}
		mElementMap.putAll(jsonObject.mElementMap);
	}
	
	/**
	 * 构造方法
	 *
	 * @param json   JSON对象
	 * @param reader JSON数据读取流
	 * @throws IOException 错误信息
	 */
	public JsonObject(Json json, Reader reader) throws IOException {
		JsonObject jsonObject = json.fromJson(reader, JsonObject.class);
		if (jsonObject == null) {
			throw new IOException("json is wrong format");
		}
		mElementMap.putAll(jsonObject.mElementMap);
	}
	
	/**
	 * 构造方法
	 *
	 * @param json       JSON对象
	 * @param jsonReader JSON读取流
	 * @throws IOException 错误信息
	 */
	public JsonObject(Json json, JsonReader jsonReader) throws IOException {
		JsonObject jsonObject = json.fromJson(jsonReader, JsonObject.class);
		mElementMap.putAll(jsonObject.mElementMap);
	}
	
	/**
	 * 获取参数数量
	 *
	 * @return 参数数量
	 */
	public int length() {
		return mElementMap.size();
	}
	
	/**
	 * 获取键对应的值
	 *
	 * @param key 键名
	 * @return 对象值
	 */
	public JsonElement get(String key) {
		return mElementMap.get(key);
	}
	
	/**
	 * 获取键对应的JsonArray值
	 *
	 * @param key 键名
	 * @return JsonArray对象值
	 * @throws IOException 错误信息
	 */
	public JsonArray getJsonArray(String key) throws IOException {
		JsonElement value = get(key);
		
		if (value instanceof JsonArray) {
			return (JsonArray) value;
		}
		
		throw new IOException("Value " + value + " at a of type " + value.getClass().getName() + " cannot be converted to JsonArray");
	}
	
	/**
	 * 获取键对应的JsonObject值
	 *
	 * @param key 键名
	 * @return JsonObject对象值
	 * @throws IOException 错误信息
	 */
	public JsonObject getJsonObject(String key) throws IOException {
		JsonElement value = get(key);
		
		if (value instanceof JsonObject) {
			return (JsonObject) value;
		}
		
		throw new IOException("Value " + value + " at a of type " + value.getClass().getName() + " cannot be converted to JsonObject");
	}
	
	/**
	 * 获取键对应的Boolean值
	 *
	 * @param key 键名
	 * @return Boolean值
	 * @throws IOException 错误信息
	 */
	public boolean getBoolean(String key) throws IOException {
		JsonElement value = get(key);
		
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
	 * 获取键对应的Double值
	 *
	 * @param key 键名
	 * @return Double值
	 * @throws IOException 错误信息
	 */
	public double getDouble(String key) throws IOException {
		JsonElement value = get(key);
		
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
	 * 获取键对应的Float值
	 *
	 * @param key 键名
	 * @return Float值
	 * @throws IOException 错误信息
	 */
	public float getFloat(String key) throws IOException {
		JsonElement value = get(key);
		
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
	 * 获取键对应的Long值
	 *
	 * @param key 键名
	 * @return Long值
	 * @throws IOException 错误信息
	 */
	public long getLong(String key) throws IOException {
		JsonElement value = get(key);
		
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
	 * 获取键对应的Integer值
	 *
	 * @param key 键名
	 * @return Integer值
	 * @throws IOException 错误信息
	 */
	public int getInt(String key) throws IOException {
		JsonElement value = get(key);
		
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
	 * 获取键对应的String值
	 *
	 * @param key 键名
	 * @return String值
	 */
	public String getString(String key) {
		JsonElement value = get(key);
		
		if (value instanceof JsonPrimitive) {
			return value.getAsString();
		}
		
		return String.valueOf(value);
	}
	
	/**
	 * 获取键对应的值
	 *
	 * @param key 键名
	 * @return 对象值
	 */
	public JsonElement opt(String key) {
		return get(key);
	}
	
	/**
	 * 获取键对应的值
	 *
	 * @param key      键名
	 * @param fallback 默认值
	 * @return 对象值
	 */
	public JsonElement opt(String key, JsonElement fallback) {
		JsonElement value = get(key);
		if (value == null) {
			return fallback;
		}
		return get(key);
	}
	
	/**
	 * 获取键对应的JsonArray值
	 *
	 * @param key 键名
	 * @return JsonArray对象值
	 */
	public JsonArray optJsonArray(String key) {
		try {
			return getJsonArray(key);
		} catch (IOException ignored) {
			return null;
		}
	}
	
	/**
	 * 获取键对应的JsonArray值
	 *
	 * @param key      键名
	 * @param fallback 默认值
	 * @return JsonArray对象值
	 */
	public JsonArray optJsonArray(String key, JsonArray fallback) {
		try {
			return getJsonArray(key);
		} catch (IOException ignored) {
			return fallback;
		}
	}
	
	/**
	 * 获取键对应的JsonObject值
	 *
	 * @param key 键名
	 * @return JsonObject对象值
	 */
	public JsonObject optJsonObject(String key) {
		try {
			return getJsonObject(key);
		} catch (IOException ignored) {
			return null;
		}
	}
	
	/**
	 * 获取键对应的JsonObject值
	 *
	 * @param key      键名
	 * @param fallback 默认值
	 * @return JsonObject对象值
	 */
	public JsonObject optJsonObject(String key, JsonObject fallback) {
		try {
			return getJsonObject(key);
		} catch (IOException ignored) {
			return fallback;
		}
	}
	
	/**
	 * 获取键对应的Boolean值
	 *
	 * @param key 键名
	 * @return Boolean值
	 */
	public boolean optBoolean(String key) {
		try {
			return getBoolean(key);
		} catch (IOException ignored) {
			return false;
		}
	}
	
	/**
	 * 获取键对应的Boolean值
	 *
	 * @param key      键名
	 * @param fallback 默认值
	 * @return Boolean值
	 */
	public boolean optBoolean(String key, boolean fallback) {
		try {
			return getBoolean(key);
		} catch (IOException ignored) {
			return fallback;
		}
	}
	
	/**
	 * 获取键对应的Double值
	 *
	 * @param key 键名
	 * @return Double值
	 */
	public double optDouble(String key) {
		try {
			return getDouble(key);
		} catch (IOException ignored) {
			return 0D;
		}
	}
	
	/**
	 * 获取键对应的Double值
	 *
	 * @param key      键名
	 * @param fallback 默认值
	 * @return Double值
	 */
	public double optDouble(String key, double fallback) {
		try {
			return getDouble(key);
		} catch (IOException ignored) {
			return fallback;
		}
	}
	
	/**
	 * 获取键对应的Float值
	 *
	 * @param key 键名
	 * @return Float值
	 */
	public float optFloat(String key) {
		try {
			return getFloat(key);
		} catch (IOException ignored) {
			return 0F;
		}
	}
	
	/**
	 * 获取键对应的Float值
	 *
	 * @param key      键名
	 * @param fallback 默认值
	 * @return Float值
	 */
	public float optFloat(String key, float fallback) {
		try {
			return getFloat(key);
		} catch (IOException ignored) {
			return fallback;
		}
	}
	
	/**
	 * 获取键对应的Long值
	 *
	 * @param key 键名
	 * @return Long值
	 */
	public long optLong(String key) {
		try {
			return getLong(key);
		} catch (IOException ignored) {
			return 0L;
		}
	}
	
	/**
	 * 获取键对应的Long值
	 *
	 * @param key      键名
	 * @param fallback 默认值
	 * @return Long值
	 */
	public long optLong(String key, long fallback) {
		try {
			return getLong(key);
		} catch (IOException ignored) {
			return fallback;
		}
	}
	
	/**
	 * 获取键对应的Integer值
	 *
	 * @param key 键名
	 * @return Integer值
	 */
	public int optInt(String key) {
		try {
			return getInt(key);
		} catch (IOException ignored) {
			return 0;
		}
	}
	
	/**
	 * 获取键对应的Integer值
	 *
	 * @param key      键名
	 * @param fallback 默认值
	 * @return Integer值
	 */
	public int optInt(String key, int fallback) {
		try {
			return getInt(key);
		} catch (IOException ignored) {
			return fallback;
		}
	}
	
	/**
	 * 获取键对应的String值
	 *
	 * @param key 键名
	 * @return String值
	 */
	public String optString(String key) {
		return getString(key);
	}
	
	/**
	 * 获取键对应的String值
	 *
	 * @param key      键名
	 * @param fallback 默认值
	 * @return String值
	 */
	public String optString(String key, String fallback) {
		String value = getString(key);
		
		if (value != null) {
			return value;
		}
		return fallback;
	}
	
	/**
	 * 存储对象值
	 *
	 * @param key   键名
	 * @param value 对象值
	 */
	public void put(String key, JsonElement value) {
		mElementMap.put(key, value);
	}
	
	/**
	 * 存储Boolean值
	 *
	 * @param key   键名
	 * @param value Boolean值
	 */
	public void put(String key, boolean value) {
		mElementMap.put(key, new JsonPrimitive(value));
	}
	
	/**
	 * 存储Double值
	 *
	 * @param key   键名
	 * @param value Double值
	 */
	public void put(String key, double value) {
		mElementMap.put(key, new JsonPrimitive(value));
	}
	
	/**
	 * 存储Float值
	 *
	 * @param key   键名
	 * @param value Float值
	 */
	public void put(String key, float value) {
		mElementMap.put(key, new JsonPrimitive(value));
	}
	
	/**
	 * 存储Long值
	 *
	 * @param key   键名
	 * @param value Long值
	 */
	public void put(String key, long value) {
		mElementMap.put(key, new JsonPrimitive(value));
	}
	
	/**
	 * 存储Integer值
	 *
	 * @param key   键名
	 * @param value Integer值
	 */
	public void put(String key, int value) {
		mElementMap.put(key, new JsonPrimitive(value));
	}
	
	/**
	 * 存储String值
	 *
	 * @param key   键名
	 * @param value String值
	 */
	public void put(String key, String value) {
		if (value == null) {
			mElementMap.put(key, JsonNull.INSTANCE);
		} else {
			mElementMap.put(key, new JsonPrimitive(value));
		}
	}
	
	/**
	 * 删除指定键值
	 *
	 * @param key 键名
	 * @return 删除值
	 */
	public JsonElement remove(String key) {
		return mElementMap.remove(key);
	}
	
	/**
	 * 清空所有值
	 */
	public void clear() {
		mElementMap.clear();
	}
	
	/**
	 * 获取所有键名
	 *
	 * @return 所有键名
	 */
	public String[] keys() {
		String[] keys = new String[mElementMap.size()];
		return mElementMap.keySet().toArray(keys);
	}
	
	public Set<Map.Entry<String, JsonElement>> entrySet() {
		return mElementMap.entrySet();
	}
	
	@Override
	protected JsonObject clone() throws CloneNotSupportedException {
		return (JsonObject) super.clone();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof JsonObject && ((JsonObject) obj).mElementMap.equals(mElementMap);
	}
	
	@Override
	public int hashCode() {
		return mElementMap.hashCode();
	}
}
