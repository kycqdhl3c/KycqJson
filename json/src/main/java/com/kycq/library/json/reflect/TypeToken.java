package com.kycq.library.json.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeToken<T> {
	private final Class<? super T> rawType;
	private final Type type;
	private final int hashCode;
	
	/**
	 * Constructs a new type literal. Derives represented class from type
	 * parameter.
	 * <p>
	 * <p>Clients create an empty anonymous subclass. Doing so embeds the type
	 * parameter in the anonymous class's type hierarchy so we can reconstitute it
	 * at runtime despite erasure.
	 */
	@SuppressWarnings("unchecked")
	protected TypeToken() {
		this.type = getSuperclassTypeParameter(getClass());
		this.rawType = (Class<? super T>) GsonTypes.getRawType(type);
		this.hashCode = type.hashCode();
	}
	
	/**
	 * Unsafe. Constructs a type literal manually.
	 */
	@SuppressWarnings("unchecked")
	TypeToken(Type type) {
		this.type = GsonTypes.canonicalize(GsonTypes.checkNotNull(type));
		this.rawType = (Class<? super T>) GsonTypes.getRawType(this.type);
		this.hashCode = this.type.hashCode();
	}
	
	/**
	 * Returns the type from super class's type parameter in {@link GsonTypes#canonicalize
	 * canonical form}.
	 */
	static Type getSuperclassTypeParameter(Class<?> subclass) {
		Type superclass = subclass.getGenericSuperclass();
		if (superclass instanceof Class) {
			throw new RuntimeException("Missing type parameter.");
		}
		ParameterizedType parameterized = (ParameterizedType) superclass;
		return GsonTypes.canonicalize(parameterized.getActualTypeArguments()[0]);
	}
	
	/**
	 * Returns the raw (non-generic) type for this type.
	 */
	public final Class<? super T> getRawType() {
		return rawType;
	}
	
	/**
	 * Gets underlying {@code Type} instance.
	 */
	public final Type getType() {
		return type;
	}
	
	@Override
	public final int hashCode() {
		return this.hashCode;
	}
	
	@Override
	public final boolean equals(Object o) {
		return o instanceof TypeToken<?>
				&& GsonTypes.equals(type, ((TypeToken<?>) o).type);
	}
	
	@Override
	public final String toString() {
		return GsonTypes.typeToString(type);
	}
	
	/**
	 * Gets type literal for the given {@code Type} instance.
	 */
	public static TypeToken<?> get(Type type) {
		return new TypeToken<Object>(type);
	}
	
	/**
	 * Gets type literal for the given {@code Class} instance.
	 */
	public static <T> TypeToken<T> get(Class<T> type) {
		return new TypeToken<T>(type);
	}
	
	/**
	 * Gets type literal for the parameterized type represented by applying {@code typeArguments} to
	 * {@code rawType}.
	 */
	public static TypeToken<?> getParameterized(Type rawType, Type... typeArguments) {
		return new TypeToken<Object>(GsonTypes.newParameterizedTypeWithOwner(null, rawType, typeArguments));
	}
	
	/**
	 * Gets type literal for the array type whose elements are all instances of {@code componentType}.
	 */
	public static TypeToken<?> getArray(Type componentType) {
		return new TypeToken<Object>(GsonTypes.arrayOf(componentType));
	}
}
