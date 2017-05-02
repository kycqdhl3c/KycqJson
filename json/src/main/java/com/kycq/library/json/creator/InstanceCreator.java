package com.kycq.library.json.creator;

import java.lang.reflect.Type;

public interface InstanceCreator<T> {
	
	T createInstance(Type type);
}
