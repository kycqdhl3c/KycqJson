package com.kycq.library.json.creator;

import com.kycq.library.json.internal.LinkedTreeMap;
import com.kycq.library.json.internal.UnsafeAllocator;
import com.kycq.library.json.reflect.TypeToken;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

@SuppressWarnings("unchecked")
public final class ConstructorCreator {
	private final Map<Type, InstanceCreator<?>> instanceCreators;
	
	public ConstructorCreator(Map<Type, InstanceCreator<?>> instanceCreators) {
		this.instanceCreators = instanceCreators;
	}
	
	public <T> ObjectConstructor<T> get(TypeToken<T> typeToken) {
		final Type type = typeToken.getType();
		final Class<? super T> rawType = typeToken.getRawType();
		
		// first try an instance creator
		
		@SuppressWarnings("unchecked") // types must agree
		final InstanceCreator<T> typeCreator = (InstanceCreator<T>) instanceCreators.get(type);
		if (typeCreator != null) {
			return new ObjectConstructor<T>() {
				@Override
				public T construct() {
					return typeCreator.createInstance(type);
				}
			};
		}
		
		// Next try raw type match for instance creators
		@SuppressWarnings("unchecked") // types must agree
		final InstanceCreator<T> rawTypeCreator =
				(InstanceCreator<T>) instanceCreators.get(rawType);
		if (rawTypeCreator != null) {
			return new ObjectConstructor<T>() {
				@Override
				public T construct() {
					return rawTypeCreator.createInstance(type);
				}
			};
		}
		
		ObjectConstructor<T> defaultConstructor = newDefaultConstructor(rawType);
		if (defaultConstructor != null) {
			return defaultConstructor;
		}
		
		ObjectConstructor<T> defaultImplementation = newDefaultImplementationConstructor(type, rawType);
		if (defaultImplementation != null) {
			return defaultImplementation;
		}
		
		// finally try unsafe
		return newUnsafeAllocator(type, rawType);
	}
	
	private <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> rawType) {
		try {
			final Constructor<? super T> constructor = rawType.getDeclaredConstructor();
			if (!constructor.isAccessible()) {
				constructor.setAccessible(true);
			}
			return new ObjectConstructor<T>() {
				@SuppressWarnings("unchecked") // T is the same raw type as is requested
				@Override
				public T construct() {
					try {
						Object[] args = null;
						return (T) constructor.newInstance(args);
					} catch (InstantiationException e) {
						throw new RuntimeException("Failed to invoke " + constructor + " with no args", e);
					} catch (InvocationTargetException e) {
						throw new RuntimeException("Failed to invoke " + constructor + " with no args",
								e.getTargetException());
					} catch (IllegalAccessException e) {
						throw new AssertionError(e);
					}
				}
			};
		} catch (NoSuchMethodException e) {
			return null;
		}
	}
	
	private <T> ObjectConstructor<T> newDefaultImplementationConstructor(
			final Type type, Class<? super T> rawType) {
		if (Collection.class.isAssignableFrom(rawType)) {
			if (SortedSet.class.isAssignableFrom(rawType)) {
				return new ObjectConstructor<T>() {
					@Override
					public T construct() {
						return (T) new TreeSet<>();
					}
				};
			} else if (EnumSet.class.isAssignableFrom(rawType)) {
				return new ObjectConstructor<T>() {
					@SuppressWarnings("rawtypes")
					@Override
					public T construct() {
						if (type instanceof ParameterizedType) {
							Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
							if (elementType instanceof Class) {
								return (T) EnumSet.noneOf((Class) elementType);
							} else {
								throw new RuntimeException("Invalid EnumSet type: " + type.toString());
							}
						} else {
							throw new RuntimeException("Invalid EnumSet type: " + type.toString());
						}
					}
				};
			} else if (Set.class.isAssignableFrom(rawType)) {
				return new ObjectConstructor<T>() {
					@Override
					public T construct() {
						return (T) new LinkedHashSet<>();
					}
				};
			} else if (Queue.class.isAssignableFrom(rawType)) {
				return new ObjectConstructor<T>() {
					@Override
					public T construct() {
						return (T) new ArrayDeque<>();
					}
				};
			} else {
				return new ObjectConstructor<T>() {
					@Override
					public T construct() {
						return (T) new ArrayList<>();
					}
				};
			}
		}
		
		if (Map.class.isAssignableFrom(rawType)) {
			if (ConcurrentNavigableMap.class.isAssignableFrom(rawType)) {
				return new ObjectConstructor<T>() {
					@Override
					public T construct() {
						return (T) new ConcurrentSkipListMap<>();
					}
				};
			} else if (ConcurrentMap.class.isAssignableFrom(rawType)) {
				return new ObjectConstructor<T>() {
					@Override
					public T construct() {
						return (T) new ConcurrentHashMap<>();
					}
				};
			} else if (SortedMap.class.isAssignableFrom(rawType)) {
				return new ObjectConstructor<T>() {
					@Override
					public T construct() {
						return (T) new TreeMap<>();
					}
				};
			} else if (type instanceof ParameterizedType && !(String.class.isAssignableFrom(
					TypeToken.get(((ParameterizedType) type).getActualTypeArguments()[0]).getRawType()))) {
				return new ObjectConstructor<T>() {
					@Override
					public T construct() {
						return (T) new LinkedHashMap<>();
					}
				};
			} else {
				return new ObjectConstructor<T>() {
					@Override
					public T construct() {
						return (T) new LinkedTreeMap<>();
					}
				};
			}
		}
		
		return null;
	}
	
	private <T> ObjectConstructor<T> newUnsafeAllocator(
			final Type type, final Class<? super T> rawType) {
		return new ObjectConstructor<T>() {
			private final UnsafeAllocator unsafeAllocator = UnsafeAllocator.create();
			
			@SuppressWarnings("unchecked")
			@Override
			public T construct() {
				try {
					Object newInstance = unsafeAllocator.newInstance(rawType);
					return (T) newInstance;
				} catch (Exception e) {
					throw new RuntimeException(("Unable to invoke no-args constructor for " + type + ". "
							+ "Register an InstanceCreator with Gson for this type may fix this problem."), e);
				}
			}
		};
	}
	
	@Override
	public String toString() {
		return instanceCreators.toString();
	}
}
