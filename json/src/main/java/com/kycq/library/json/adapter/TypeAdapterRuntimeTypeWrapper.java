/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kycq.library.json.adapter;

import com.kycq.library.json.Json;
import com.kycq.library.json.JsonException;
import com.kycq.library.json.reflect.TypeToken;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonWriter;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

final class TypeAdapterRuntimeTypeWrapper<T> extends TypeAdapter<T> {
	private final Json json;
	private final TypeAdapter<T> delegate;
	private final Type type;
	
	TypeAdapterRuntimeTypeWrapper(Json json, TypeAdapter<T> delegate, Type type) {
		this.json = json;
		this.delegate = delegate;
		this.type = type;
	}
	
	@Override
	public T read(JsonReader in) throws JsonException {
		return delegate.read(in);
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void write(JsonWriter out, T value) throws JsonException {
		// Order of preference for choosing type adapters
		// First preference: a type adapter registered for the runtime type
		// Second preference: a type adapter registered for the declared type
		// Third preference: reflective type adapter for the runtime type (if it is a sub class of the declared type)
		// Fourth preference: reflective type adapter for the declared type
		
		TypeAdapter chosen = delegate;
		Type runtimeType = getRuntimeTypeIfMoreSpecific(type, value);
		if (runtimeType != type) {
			TypeAdapter runtimeTypeAdapter = this.json.getAdapter(TypeToken.get(runtimeType));
			if (!(runtimeTypeAdapter instanceof ReflectiveTypeAdapter)) {
				// The user registered a type adapter for the runtime type, so we will use that
				chosen = runtimeTypeAdapter;
			} else if (!(delegate instanceof ReflectiveTypeAdapter)) {
				// The user registered a type adapter for Base class, so we prefer it over the
				// reflective type adapter for the runtime type
				chosen = delegate;
			} else {
				// Use the type adapter for runtime type
				chosen = runtimeTypeAdapter;
			}
		}
		chosen.write(out, value);
	}
	
	/**
	 * Finds a compatible runtime type if it is more specific
	 */
	private Type getRuntimeTypeIfMoreSpecific(Type type, Object value) {
		if (value != null
				&& (type == Object.class || type instanceof TypeVariable<?> || type instanceof Class<?>)) {
			type = value.getClass();
		}
		return type;
	}
}
