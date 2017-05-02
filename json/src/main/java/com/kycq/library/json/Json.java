package com.kycq.library.json;

import com.kycq.library.json.adapter.ArrayTypeAdapterFactory;
import com.kycq.library.json.adapter.CollectionTypeAdapterFactory;
import com.kycq.library.json.adapter.EnumTypeAdapterFactory;
import com.kycq.library.json.adapter.JsonElementAdapterFactory;
import com.kycq.library.json.adapter.MapTypeAdapterFactory;
import com.kycq.library.json.adapter.ObjectTypeAdapterFactory;
import com.kycq.library.json.adapter.ReflectiveTypeAdapterFactory;
import com.kycq.library.json.adapter.TypeAdapter;
import com.kycq.library.json.adapter.TypeAdapterFactory;
import com.kycq.library.json.adapter.basic.AtomicBooleanAdapter;
import com.kycq.library.json.adapter.basic.AtomicIntegerAdapter;
import com.kycq.library.json.adapter.basic.AtomicIntegerArrayAdapter;
import com.kycq.library.json.adapter.basic.BigDecimalAdapter;
import com.kycq.library.json.adapter.basic.BigIntegerAdapter;
import com.kycq.library.json.adapter.basic.BitSetAdapter;
import com.kycq.library.json.adapter.basic.BooleanAdapter;
import com.kycq.library.json.adapter.basic.ByteAdapter;
import com.kycq.library.json.adapter.basic.CharacterAdapter;
import com.kycq.library.json.adapter.basic.ClassAdapter;
import com.kycq.library.json.adapter.basic.DoubleAdapter;
import com.kycq.library.json.adapter.basic.FloatAdapter;
import com.kycq.library.json.adapter.basic.IntegerAdapter;
import com.kycq.library.json.adapter.basic.LongAdapter;
import com.kycq.library.json.adapter.basic.NumberAdapter;
import com.kycq.library.json.adapter.basic.ShortAdapter;
import com.kycq.library.json.adapter.basic.StringAdapter;
import com.kycq.library.json.adapter.basic.StringBufferAdapter;
import com.kycq.library.json.adapter.basic.StringBuilderAdapter;
import com.kycq.library.json.adapter.basic.URIAdapter;
import com.kycq.library.json.adapter.basic.URLAdapter;
import com.kycq.library.json.creator.ConstructorCreator;
import com.kycq.library.json.creator.InstanceCreator;
import com.kycq.library.json.reflect.TypeToken;
import com.kycq.library.json.stream.JsonReader;
import com.kycq.library.json.stream.JsonWriter;

import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class Json {
	private static final TypeToken<?> NULL_KEY_SURROGATE = TypeToken.get(Object.class);
	
	private final ThreadLocal<Map<TypeToken<?>, FutureTypeAdapter<?>>> calls = new ThreadLocal<>();
	private final Map<TypeToken<?>, TypeAdapter<?>> typeTokenCache = new ConcurrentHashMap<>();
	
	private final List<TypeAdapterFactory> adapterFactoryList;
	
	private ConstructorCreator constructorCreator;
	
	public Json() {
		this(Collections.EMPTY_LIST, Collections.EMPTY_MAP);
	}
	
	public Json(List<TypeAdapterFactory> typeAdapterFactoryList,
	            Map<Type, InstanceCreator<?>> instanceCreators) {
		List<TypeAdapterFactory> adapterFactoryList = new ArrayList<>();
		
		adapterFactoryList.add(JsonElementAdapterFactory.FACTORY);
		adapterFactoryList.add(ObjectTypeAdapterFactory.FACTORY);
		
		adapterFactoryList.addAll(typeAdapterFactoryList);
		
		// type adapters for basic platform types
		adapterFactoryList.add(StringAdapter.FACTORY);
		adapterFactoryList.add(IntegerAdapter.FACTORY);
		adapterFactoryList.add(BooleanAdapter.FACTORY);
		adapterFactoryList.add(ByteAdapter.FACTORY);
		adapterFactoryList.add(ShortAdapter.FACTORY);
		adapterFactoryList.add(LongAdapter.FACTORY);
		adapterFactoryList.add(DoubleAdapter.FACTORY);
		adapterFactoryList.add(FloatAdapter.FACTORY);
		adapterFactoryList.add(NumberAdapter.FACTORY);
		adapterFactoryList.add(AtomicIntegerAdapter.FACTORY);
		adapterFactoryList.add(AtomicBooleanAdapter.FACTORY);
		// adapterFactoryList.add(TypeAdapters.newFactory(AtomicLong.class, atomicLongAdapter(longAdapter)));
		// adapterFactoryList.add(TypeAdapters.newFactory(AtomicLongArray.class, atomicLongArrayAdapter(longAdapter)));
		adapterFactoryList.add(AtomicIntegerArrayAdapter.FACTORY);
		adapterFactoryList.add(CharacterAdapter.FACTORY);
		adapterFactoryList.add(StringBuilderAdapter.FACTORY);
		adapterFactoryList.add(StringBufferAdapter.FACTORY);
		adapterFactoryList.add(BigDecimalAdapter.FACTORY);
		adapterFactoryList.add(BigIntegerAdapter.FACTORY);
		adapterFactoryList.add(URLAdapter.FACTORY);
		adapterFactoryList.add(URIAdapter.FACTORY);
		// adapterFactoryList.add(UUIDAdapterFactory.INSTANCE);
		// adapterFactoryList.add(CurrencyAdapterFactory.INSTANCE);
		// adapterFactoryList.add(LocaleAdapterFactory.INSTANCE);
		// adapterFactoryList.add(InetAddressAdapterFactory.INSTANCE);
		adapterFactoryList.add(BitSetAdapter.FACTORY);
		// adapterFactoryList.add(DateTypeAdapter.FACTORY);
		// adapterFactoryList.add(CalendarAdapterFactory.INSTANCE);
		// adapterFactoryList.add(TimeTypeAdapter.FACTORY);
		// adapterFactoryList.add(SqlDateTypeAdapter.FACTORY);
		// adapterFactoryList.add(TypeAdapters.TIMESTAMP_FACTORY);
		adapterFactoryList.add(ArrayTypeAdapterFactory.FACTORY);
		adapterFactoryList.add(ClassAdapter.FACTORY);
		
		// type adapters for composite and user-defined types
		adapterFactoryList.add(CollectionTypeAdapterFactory.FACTORY);
		adapterFactoryList.add(MapTypeAdapterFactory.FACTORY);
		// this.jsonAdapterFactory = new JsonAdapterAnnotationTypeAdapterFactory(constructorConstructor);
		// adapterFactoryList.add(jsonAdapterFactory);
		adapterFactoryList.add(EnumTypeAdapterFactory.FACTORY);
		adapterFactoryList.add(new ReflectiveTypeAdapterFactory());
		
		this.adapterFactoryList = Collections.unmodifiableList(adapterFactoryList);
		
		this.constructorCreator = new ConstructorCreator(Collections.unmodifiableMap(instanceCreators));
	}
	
	public <T> T fromJson(String jsonStr, Class<T> clazz) throws JsonException {
		return fromJson(new JsonReader(jsonStr), TypeToken.get(clazz));
	}
	
	public <T> T fromJson(String jsonStr, Type type) throws JsonException {
		return (T) fromJson(new JsonReader(jsonStr), TypeToken.get(type));
	}
	
	public <T> T fromJson(String jsonStr, TypeToken<T> typeToken) throws JsonException {
		return fromJson(new JsonReader(jsonStr), typeToken);
	}
	
	public <T> T fromJson(Reader reader, Class<T> clazz) throws JsonException {
		return fromJson(new JsonReader(reader), TypeToken.get(clazz));
	}
	
	public <T> T fromJson(Reader reader, Type type) throws JsonException {
		return (T) fromJson(new JsonReader(reader), TypeToken.get(type));
	}
	
	public <T> T fromJson(Reader reader, TypeToken<T> typeToken) throws JsonException {
		return fromJson(new JsonReader(reader), typeToken);
	}
	
	public <T> T fromJson(JsonReader jsonReader, Class<T> clazz) throws JsonException {
		return fromJson(jsonReader, TypeToken.get(clazz));
	}
	
	public <T> T fromJson(JsonReader jsonReader, Type type) throws JsonException {
		return (T) fromJson(jsonReader, TypeToken.get(type));
	}
	
	public <T> T fromJson(JsonReader jsonReader, TypeToken<T> typeToken) throws JsonException {
		TypeAdapter<T> typeAdapter = getAdapter(typeToken);
		return typeAdapter.read(jsonReader);
	}
	
	public String toJson(Object value) throws JsonException {
		TypeAdapter<Object> typeAdapter = (TypeAdapter<Object>) getAdapter(TypeToken.get(value.getClass()));
		StringWriter stringWriter = new StringWriter();
		JsonWriter jsonWriter = new JsonWriter(stringWriter);
		typeAdapter.write(jsonWriter, value);
		return stringWriter.toString();
	}
	
	public <T> TypeAdapter<T> getAdapter(Class<T> type) {
		return getAdapter(TypeToken.get(type));
	}
	
	public <T> TypeAdapter<T> getAdapter(TypeToken<T> typeToken) {
		TypeAdapter<?> cached = this.typeTokenCache.get(typeToken == null ? NULL_KEY_SURROGATE : typeToken);
		if (cached != null) {
			return (TypeAdapter<T>) cached;
		}
		
		Map<TypeToken<?>, FutureTypeAdapter<?>> threadCalls = calls.get();
		boolean requiresThreadLocalCleanup = false;
		if (threadCalls == null) {
			threadCalls = new HashMap<>();
			this.calls.set(threadCalls);
			requiresThreadLocalCleanup = true;
		}
		
		FutureTypeAdapter<T> ongoingCall = (FutureTypeAdapter<T>) threadCalls.get(typeToken);
		if (ongoingCall != null) {
			return ongoingCall;
		}
		
		try {
			FutureTypeAdapter<T> call = new FutureTypeAdapter<T>();
			threadCalls.put(typeToken, call);
			
			for (TypeAdapterFactory factory : this.adapterFactoryList) {
				TypeAdapter<T> candidate = factory.create(this, typeToken);
				if (candidate != null) {
					call.setDelegate(candidate);
					this.typeTokenCache.put(typeToken, candidate);
					return candidate;
				}
			}
			throw new IllegalArgumentException("Json cannot handle " + typeToken);
		} finally {
			threadCalls.remove(typeToken);
			
			if (requiresThreadLocalCleanup) {
				this.calls.remove();
			}
		}
	}
	
	public ConstructorCreator getConstructorCreator() {
		return this.constructorCreator;
	}
	
	private static class FutureTypeAdapter<T> extends TypeAdapter<T> {
		private TypeAdapter<T> delegate;
		
		void setDelegate(TypeAdapter<T> typeAdapter) {
			if (this.delegate != null) {
				throw new AssertionError();
			}
			this.delegate = typeAdapter;
		}
		
		@Override
		public T read(JsonReader jsonReader) throws JsonException {
			if (this.delegate == null) {
				throw new IllegalStateException();
			}
			return this.delegate.read(jsonReader);
		}
		
		@Override
		public void write(JsonWriter out, T value) throws JsonException {
			if (this.delegate == null) {
				throw new IllegalStateException();
			}
			this.delegate.write(out, value);
		}
	}
}
