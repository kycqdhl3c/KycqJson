package com.kycq.library.json.adapter;

import com.kycq.library.json.Json;
import com.kycq.library.json.reflect.TypeToken;

public interface TypeAdapterFactory {
	
	<T> TypeAdapter<T> create(Json json, TypeToken<T> typeToken);
}
