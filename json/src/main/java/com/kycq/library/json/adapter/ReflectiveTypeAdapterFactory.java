package com.kycq.library.json.adapter;

import com.kycq.library.json.Json;
import com.kycq.library.json.JsonException;
import com.kycq.library.json.JsonLog;
import com.kycq.library.json.annotation.JsonAdapter;
import com.kycq.library.json.annotation.JsonIgnore;
import com.kycq.library.json.annotation.JsonName;
import com.kycq.library.json.creator.ObjectConstructor;
import com.kycq.library.json.internal.Primitives;
import com.kycq.library.json.reflect.GsonTypes;
import com.kycq.library.json.reflect.TypeToken;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonWriter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReflectiveTypeAdapterFactory implements TypeAdapterFactory {
	
	@Override
	public <T> TypeAdapter<T> create(Json json, TypeToken<T> typeToken) {
		Class<? super T> rawType = typeToken.getRawType();
		
		if (!Object.class.isAssignableFrom(rawType)) {
			return null;
		}
		
		ObjectConstructor<T> constructor = json.getConstructorCreator().get(typeToken);
		return new ReflectiveTypeAdapter<>(constructor, getBoundFields(json, typeToken, rawType));
	}
	
	private Map<String, BoundField> getBoundFields(Json json, TypeToken<?> typeToken, Class<?> rawType) {
		Map<String, BoundField> boundFieldMap = new LinkedHashMap<>();
		
		if (rawType.isInterface()) {
			return boundFieldMap;
		}
		
		Type declaredType = typeToken.getType();
		while (rawType != Object.class) {
			Field[] fields = rawType.getDeclaredFields();
			for (Field field : fields) {
				boolean serialize = includeField(field, true);
				boolean deserialize = includeField(field, false);
				if (!serialize && !deserialize) {
					continue;
				}
				
				field.setAccessible(true);
				Type fieldType = GsonTypes.resolve(typeToken.getType(), rawType, field.getGenericType());
				TypeToken fieldTypeToken = TypeToken.get(fieldType);
				List<String> fieldNameList = getFieldNameList(field);
				
				final boolean isPrimitive = Primitives.isPrimitive(fieldTypeToken.getRawType());
				TypeAdapter<?> fieldTypeAdapter = getAdapter(json, field, fieldTypeToken);// json.getAdapter(fieldTypeToken);
				
				for (int index = 0; index < fieldNameList.size(); index++) {
					String fieldName = fieldNameList.get(index);
					
					BoundField boundField = new BoundField(
							isPrimitive,
							serialize, deserialize,
							fieldName, field,
							fieldTypeAdapter
					);
					
					BoundField replaced = boundFieldMap.put(fieldName, boundField);
					if (replaced != null) {
						throw new IllegalArgumentException(declaredType
								+ " declares multiple fields jsonName named " + replaced.fieldName);
					}
				}
			}
			typeToken = TypeToken.get(GsonTypes.resolve(typeToken.getType(), rawType, rawType.getGenericSuperclass()));
			rawType = typeToken.getRawType();
		}
		
		return boundFieldMap;
	}
	
	private boolean includeField(Field field, boolean serialize) {
		int modifiers = field.getModifiers();
		if (Modifier.isFinal(modifiers)
				|| Modifier.isStatic(field.getModifiers())
				|| Modifier.isNative(field.getModifiers())
				|| Modifier.isTransient(field.getModifiers())
				|| Modifier.isVolatile(field.getModifiers())) {
			return false;
		}
		if (field.isSynthetic()) {
			return false;
		}
		
		JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);
		if (jsonIgnore != null) {
			if (serialize ? !jsonIgnore.serialize() : !jsonIgnore.deserialize()) {
				return false;
			}
		}
		
		Class<?> fieldClazz = field.getType();
		return Enum.class.isAssignableFrom(fieldClazz)
				|| !(fieldClazz.isAnonymousClass()
				|| fieldClazz.isLocalClass());
	}
	
	private List<String> getFieldNameList(Field field) {
		JsonName jsonName = field.getAnnotation(JsonName.class);
		if (jsonName == null) {
			String name = field.getName();
			return Collections.singletonList(name);
		}
		
		String serializedName = jsonName.value();
		String[] alternates = jsonName.alternate();
		if (alternates.length == 0) {
			return Collections.singletonList(serializedName);
		}
		
		List<String> fieldNames = new ArrayList<>(alternates.length + 1);
		fieldNames.add(serializedName);
		Collections.addAll(fieldNames, alternates);
		return fieldNames;
	}
	
	private TypeAdapter<?> getAdapter(Json json, Field field, TypeToken<?> typeToken) {
		JsonAdapter jsonAdapter = field.getAnnotation(JsonAdapter.class);
		if (jsonAdapter == null) {
			return json.getAdapter(typeToken);
		}
		
		Class<?> clazz = jsonAdapter.value();
		return (TypeAdapter<?>) json.getConstructorCreator().get(TypeToken.get(clazz)).construct();
	}
	
	static class BoundField {
		private static final String TAG = BoundField.class.getName();
		
		boolean isPrimitive;
		boolean isSerialize;
		boolean isDeserialize;
		
		String fieldName;
		Field field;
		
		TypeAdapter<?> fieldTypeAdapter;
		
		BoundField(boolean isPrimitive,
		           boolean isSerialize, boolean isDeserialize,
		           String fieldName, Field field,
		           TypeAdapter<?> fieldTypeAdapter) {
			this.isPrimitive = isPrimitive;
			this.isSerialize = isSerialize;
			this.isDeserialize = isDeserialize;
			
			this.fieldName = fieldName;
			this.field = field;
			
			this.fieldTypeAdapter = fieldTypeAdapter;
		}
		
		void read(JsonReader jsonReader, Object objectValue) throws JsonException {
			try {
				Object fieldValue = fieldTypeAdapter.read(jsonReader);
				if (fieldValue != null || !isPrimitive) {
					field.set(objectValue, fieldValue);
				}
			} catch (Exception e) {
				JsonLog.e(TAG, "read " + objectValue.getClass() + "'s field " + fieldName + ", Error Message: " + e.getMessage());
			}
		}
		
		boolean readField() {
			return isDeserialize;
		}
		
		@SuppressWarnings("unchecked")
		void write(JsonWriter jsonWriter, Object objectValue) throws JsonException {
			try {
				Object fieldValue = field.get(objectValue);
				((TypeAdapter<Object>) fieldTypeAdapter).write(jsonWriter, fieldValue);
			} catch (Exception e) {
				JsonLog.e(TAG, "write " + objectValue.getClass() + "'s field " + fieldName + ", Error Message: " + e.getMessage());
			}
		}
		
		boolean writeField(Object value) {
			if (!isSerialize) {
				return false;
			}
			try {
				Object fieldValue = field.get(value);
				return fieldValue != value;
			} catch (IllegalAccessException ignored) {
			}
			return false;
		}
	}
}
