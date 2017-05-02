package com.kycq.library.json;

import com.kycq.library.json.adapter.JsonElementAdapter;
import com.kycq.library.json.stream.JsonWriter;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class JsonElement {
	
	public boolean isJsonArray() {
		return this instanceof JsonArray;
	}
	
	public boolean isJsonObject() {
		return this instanceof JsonObject;
	}
	
	public boolean isJsonPrimitive() {
		return this instanceof JsonPrimitive;
	}
	
	public boolean isJsonNull() {
		return this instanceof JsonNull;
	}
	
	public JsonArray getAsJsonArray() {
		if (isJsonArray()) {
			return (JsonArray) this;
		}
		throw new IllegalStateException("This is not a JsonArray: " + this);
	}
	
	public JsonObject getAsJsonObject() {
		if (isJsonObject()) {
			return (JsonObject) this;
		}
		throw new IllegalStateException("This is not a JsonObject: " + this);
	}
	
	public JsonPrimitive getAsJsonPrimitive() {
		if (isJsonPrimitive()) {
			return (JsonPrimitive) this;
		}
		throw new IllegalStateException("This is not a JsonPrimitive: " + this);
	}
	
	public JsonNull getAsJsonNull() {
		if (isJsonNull()) {
			return (JsonNull) this;
		}
		throw new IllegalStateException("This is not a JsonNull: " + this);
	}
	
	public boolean getAsBoolean() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}
	
	Boolean getAsBooleanWrapper() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}
	
	public Number getAsNumber() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}
	
	public BigDecimal getAsBigDecimal() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}
	
	public BigInteger getAsBigInteger() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}
	
	public double getAsDouble() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}
	
	public float getAsFloat() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}
	
	public long getAsLong() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}
	
	public int getAsInt() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}
	
	public short getAsShort() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}
	
	public byte getAsByte() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}
	
	public char getAsCharacter() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}
	
	public String getAsString() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}
	
	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter();
		try {
			JsonWriter jsonWriter = new JsonWriter(stringWriter);
			JsonElementAdapter.ADAPTER.write(jsonWriter, this);
		} catch (Exception ignored) {
		}
		return stringWriter.toString();
	}
}
