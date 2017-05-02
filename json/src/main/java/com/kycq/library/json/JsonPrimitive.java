package com.kycq.library.json;

import com.kycq.library.json.internal.LazilyParsedNumber;

import java.math.BigDecimal;
import java.math.BigInteger;

public class JsonPrimitive extends JsonElement {
	private static final Class<?>[] PRIMITIVE_TYPES = {
			boolean.class,
			Boolean.class,
			double.class,
			Double.class,
			float.class,
			Float.class,
			long.class,
			Long.class,
			int.class,
			Integer.class,
			short.class,
			Short.class,
			byte.class,
			Byte.class,
			char.class,
			Character.class
	};
	
	private Object value;
	
	public JsonPrimitive(Boolean bool) {
		setValue(bool);
	}
	
	public JsonPrimitive(Number number) {
		setValue(number);
	}
	
	public JsonPrimitive(Character c) {
		setValue(c);
	}
	
	public JsonPrimitive(String string) {
		setValue(string);
	}
	
	JsonPrimitive(Object primitive) {
		setValue(primitive);
	}
	
	public boolean isBoolean() {
		return value instanceof Boolean;
	}
	
	@Override
	public boolean getAsBoolean() {
		if (isBoolean()) {
			return getAsBooleanWrapper();
		} else {
			// Check to see if the value as a String is "true" in any case.
			return Boolean.parseBoolean(getAsString());
		}
	}
	
	@Override
	Boolean getAsBooleanWrapper() {
		return (Boolean) value;
	}
	
	public boolean isNumber() {
		return value instanceof Number;
	}
	
	@Override
	public Number getAsNumber() {
		return value instanceof String ? new LazilyParsedNumber((String) value) : (Number) value;
	}
	
	@Override
	public BigDecimal getAsBigDecimal() {
		return value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(value.toString());
	}
	
	@Override
	public BigInteger getAsBigInteger() {
		return value instanceof BigInteger ? (BigInteger) value : new BigInteger(value.toString());
	}
	
	@Override
	public double getAsDouble() {
		return isNumber() ? getAsNumber().doubleValue() : Double.parseDouble(getAsString());
	}
	
	@Override
	public float getAsFloat() {
		return isNumber() ? getAsNumber().floatValue() : Float.parseFloat(getAsString());
	}
	
	@Override
	public long getAsLong() {
		return isNumber() ? getAsNumber().longValue() : Long.parseLong(getAsString());
	}
	
	@Override
	public int getAsInt() {
		return isNumber() ? getAsNumber().intValue() : Integer.parseInt(getAsString());
	}
	
	@Override
	public short getAsShort() {
		return isNumber() ? getAsNumber().shortValue() : Short.parseShort(getAsString());
	}
	
	@Override
	public byte getAsByte() {
		return isNumber() ? getAsNumber().byteValue() : Byte.parseByte(getAsString());
	}
	
	@Override
	public char getAsCharacter() {
		return getAsString().charAt(0);
	}
	
	public boolean isString() {
		return value instanceof String;
	}
	
	@Override
	public String getAsString() {
		if (isNumber()) {
			return getAsNumber().toString();
		} else if (isBoolean()) {
			return getAsBooleanWrapper().toString();
		} else {
			return (String) value;
		}
	}
	
	void setValue(Object primitive) {
		if (primitive instanceof Character) {
			// convert characters to strings since in Json, characters are represented as a single
			// character string
			char c = (Character) primitive;
			this.value = String.valueOf(c);
		} else {
			if (primitive instanceof Number || isPrimitiveOrString(primitive)) {
				this.value = primitive;
			} else {
				throw new IllegalArgumentException();
			}
		}
	}
	
	@Override
	public int hashCode() {
		if (value == null) {
			return 31;
		}
		// Using recommended hashing algorithm from Effective Java for longs and doubles
		if (isIntegral(this)) {
			long value = getAsNumber().longValue();
			return (int) (value ^ (value >>> 32));
		}
		if (value instanceof Number) {
			long value = Double.doubleToLongBits(getAsNumber().doubleValue());
			return (int) (value ^ (value >>> 32));
		}
		return value.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		JsonPrimitive other = (JsonPrimitive) obj;
		if (value == null) {
			return other.value == null;
		}
		if (isIntegral(this) && isIntegral(other)) {
			return getAsNumber().longValue() == other.getAsNumber().longValue();
		}
		if (value instanceof Number && other.value instanceof Number) {
			double a = getAsNumber().doubleValue();
			// Java standard types other than double return true for two NaN. So, need
			// special handling for double.
			double b = other.getAsNumber().doubleValue();
			return a == b || (Double.isNaN(a) && Double.isNaN(b));
		}
		return value.equals(other.value);
	}
	
	/**
	 * Returns true if the specified number is an integral type
	 * (Long, Integer, Short, Byte, BigInteger)
	 */
	private static boolean isIntegral(JsonPrimitive primitive) {
		if (primitive.value instanceof Number) {
			Number number = (Number) primitive.value;
			return number instanceof BigInteger || number instanceof Long || number instanceof Integer
					|| number instanceof Short || number instanceof Byte;
		}
		return false;
	}
	
	private static boolean isPrimitiveOrString(Object target) {
		if (target instanceof String) {
			return true;
		}
		
		Class<?> classOfPrimitive = target.getClass();
		for (Class<?> standardPrimitive : PRIMITIVE_TYPES) {
			if (standardPrimitive.isAssignableFrom(classOfPrimitive)) {
				return true;
			}
		}
		return false;
	}
}
