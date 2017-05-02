package com.kycq.library.json;

public class JsonNull extends JsonElement {
	public static final JsonNull INSTANCE = new JsonNull();
	
	private JsonNull() {
	}
	
	@Override
	public int hashCode() {
		return JsonNull.class.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof JsonNull;
	}
}
