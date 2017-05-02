package com.kycq.library.json;

import com.kycq.library.json.bean.AdapterBean;
import com.kycq.library.json.bean.ArrayIntegerBean;
import com.kycq.library.json.bean.ArrayStringBean;
import com.kycq.library.json.bean.AtomicBooleanBean;
import com.kycq.library.json.bean.AtomicIntegerArrayBean;
import com.kycq.library.json.bean.AtomicIntegerBean;
import com.kycq.library.json.bean.BigDecimalBean;
import com.kycq.library.json.bean.BigIntegerBean;
import com.kycq.library.json.bean.BitSetBean;
import com.kycq.library.json.bean.BooleanBean;
import com.kycq.library.json.bean.ByteBean;
import com.kycq.library.json.bean.CharacterBean;
import com.kycq.library.json.bean.CollectionIntegerBean;
import com.kycq.library.json.bean.CollectionStringBean;
import com.kycq.library.json.bean.DoubleBean;
import com.kycq.library.json.bean.EnumBean;
import com.kycq.library.json.bean.EnumValue;
import com.kycq.library.json.bean.FloatBean;
import com.kycq.library.json.bean.IntegerBean;
import com.kycq.library.json.bean.LongBean;
import com.kycq.library.json.bean.MapBean;
import com.kycq.library.json.bean.NumberBean;
import com.kycq.library.json.bean.ShortBean;
import com.kycq.library.json.bean.StringBean;
import com.kycq.library.json.bean.StringBufferBean;
import com.kycq.library.json.bean.StringBuilderBean;
import com.kycq.library.json.bean.URIBean;
import com.kycq.library.json.bean.URLBean;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BasicAdapterTest {
	Json json;
	
	@Before
	public void setUp() {
		json = new Json();
	}
	
	@Test
	public void testAtomicBooleanAdapter() throws IOException {
		AtomicBooleanBean atomicBooleanBean;
		atomicBooleanBean = json.fromJson("{\"atomicBoolean\":true}", AtomicBooleanBean.class);
		assertEquals(atomicBooleanBean.atomicBoolean.get(), true);
		
		atomicBooleanBean = json.fromJson("{\"atomicBoolean\":false}", AtomicBooleanBean.class);
		assertEquals(atomicBooleanBean.atomicBoolean.get(), false);
		
		atomicBooleanBean = json.fromJson("{\"atomicBoolean\":\"true\"}", AtomicBooleanBean.class);
		assertEquals(atomicBooleanBean.atomicBoolean.get(), true);
		
		atomicBooleanBean = json.fromJson("{\"atomicBoolean\":\"false\"}", AtomicBooleanBean.class);
		assertEquals(atomicBooleanBean.atomicBoolean.get(), false);
		
		atomicBooleanBean = json.fromJson("{\"atomicBoolean\":1}", AtomicBooleanBean.class);
		assertEquals(atomicBooleanBean.atomicBoolean, null);
		
		atomicBooleanBean = json.fromJson("{\"atomicBoolean\":0}", AtomicBooleanBean.class);
		assertEquals(atomicBooleanBean.atomicBoolean, null);
		
		atomicBooleanBean = json.fromJson("{\"atomicBoolean\":\"false a\"}", AtomicBooleanBean.class);
		assertEquals(atomicBooleanBean.atomicBoolean, null);
		
		atomicBooleanBean = json.fromJson("{\"atomicBoolean\":null}", AtomicBooleanBean.class);
		assertEquals(atomicBooleanBean.atomicBoolean, null);
	}
	
	@Test
	public void testAtomicIntegerAdapter() throws IOException {
		AtomicIntegerBean atomicIntegerBean;
		
		atomicIntegerBean = json.fromJson("{\"atomicInteger\":1}", AtomicIntegerBean.class);
		assertEquals(atomicIntegerBean.atomicInteger.get(), 1);
		
		atomicIntegerBean = json.fromJson("{\"atomicInteger\":\"1\"}", AtomicIntegerBean.class);
		assertEquals(atomicIntegerBean.atomicInteger.get(), 1);
		
		atomicIntegerBean = json.fromJson("{\"atomicInteger\":9999999999999999999999999}", AtomicIntegerBean.class);
		assertEquals(atomicIntegerBean.atomicInteger, null);
		
		atomicIntegerBean = json.fromJson("{\"atomicInteger\":\"A\"}", AtomicIntegerBean.class);
		assertEquals(atomicIntegerBean.atomicInteger, null);
	}
	
	@Test
	public void testAtomicIntegerArrayAdapter() throws IOException {
		AtomicIntegerArrayBean atomicIntegerArrayBean = json.fromJson("{\"atomicIntegerArray\":[1,2]}", AtomicIntegerArrayBean.class);
		int[] value1 = new int[]{1, 2};
		for (int index = 0; index < atomicIntegerArrayBean.atomicIntegerArray.length(); index++) {
			assertEquals(atomicIntegerArrayBean.atomicIntegerArray.get(index), value1[index]);
		}
		
		atomicIntegerArrayBean = json.fromJson("{\"atomicIntegerArray\":1}", AtomicIntegerArrayBean.class);
		assertEquals(atomicIntegerArrayBean.atomicIntegerArray, null);
		
		atomicIntegerArrayBean = json.fromJson("{\"atomicIntegerArray\":{\"value\":1}}", AtomicIntegerArrayBean.class);
		assertEquals(atomicIntegerArrayBean.atomicIntegerArray, null);
		
		atomicIntegerArrayBean = json.fromJson("{\"atomicIntegerArray\":null}", AtomicIntegerArrayBean.class);
		assertEquals(atomicIntegerArrayBean.atomicIntegerArray, null);
	}
	
	@Test
	public void testBigDecimalAdapter() throws IOException {
		BigDecimalBean bigDecimalBean = json.fromJson("{\"bigDecimal\":111111}", BigDecimalBean.class);
		assertTrue(bigDecimalBean.bigDecimal.equals(new BigDecimal(111111)));
		
		bigDecimalBean = json.fromJson("{\"bigDecimal\":\"111111\"}", BigDecimalBean.class);
		assertTrue(bigDecimalBean.bigDecimal.equals(new BigDecimal(111111)));
		
		bigDecimalBean = json.fromJson("{\"bigDecimal\":1111111111111111111111111}", BigDecimalBean.class);
		assertTrue(bigDecimalBean.bigDecimal.equals(new BigDecimal("1111111111111111111111111")));
		
		bigDecimalBean = json.fromJson("{\"bigDecimal\":111111.1231232111111111111}", BigDecimalBean.class);
		assertTrue(bigDecimalBean.bigDecimal.equals(new BigDecimal("111111.1231232111111111111")));
		
		bigDecimalBean = json.fromJson("{\"bigDecimal\":\"21321E7\"}", BigDecimalBean.class);
		assertTrue(bigDecimalBean.bigDecimal.equals(new BigDecimal("21321E7")));
		
		bigDecimalBean = json.fromJson("{\"bigDecimal\":\"test\"}", BigDecimalBean.class);
		assertEquals(bigDecimalBean.bigDecimal, null);
		
		bigDecimalBean = json.fromJson("{\"bigDecimal\":null}", BigDecimalBean.class);
		assertEquals(bigDecimalBean.bigDecimal, null);
	}
	
	@Test
	public void testBigIntegerAdapter() throws IOException {
		BigIntegerBean bigIntegerBean = json.fromJson("{\"bigInteger\":111111}", BigIntegerBean.class);
		assertTrue(bigIntegerBean.bigInteger.equals(new BigInteger("111111")));
		
		bigIntegerBean = json.fromJson("{\"bigInteger\":\"111111\"}", BigIntegerBean.class);
		assertTrue(bigIntegerBean.bigInteger.equals(new BigInteger("111111")));
		
		bigIntegerBean = json.fromJson("{\"bigInteger\":1111111111111111111111111}", BigIntegerBean.class);
		assertTrue(bigIntegerBean.bigInteger.equals(new BigInteger("1111111111111111111111111")));
		
		bigIntegerBean = json.fromJson("{\"bigInteger\":111111.123123}", BigIntegerBean.class);
		assertEquals(bigIntegerBean.bigInteger, null);
		
		bigIntegerBean = json.fromJson("{\"bigInteger\":\"21321E7\"}", BigIntegerBean.class);
		assertEquals(bigIntegerBean.bigInteger, null);
		
		bigIntegerBean = json.fromJson("{\"bigInteger\":\"test\"}", BigIntegerBean.class);
		assertEquals(bigIntegerBean.bigInteger, null);
		
		bigIntegerBean = json.fromJson("{\"bigInteger\":null}", BigIntegerBean.class);
		assertEquals(bigIntegerBean.bigInteger, null);
	}
	
	@Test
	public void testBitSetAdapter() throws IOException {
		BitSetBean bitSetBean;
		boolean[] bitArray = new boolean[]{true, true, false};
		
		bitSetBean = json.fromJson("{\"bitSet\":[1,1,0]}", BitSetBean.class);
		for (int index = 0; index < bitSetBean.bitSet.length(); index++) {
			assertEquals(bitSetBean.bitSet.get(index), bitArray[index]);
		}
		
		bitSetBean = json.fromJson("{\"bitSet\":[\"1\",1,0]}", BitSetBean.class);
		for (int index = 0; index < bitSetBean.bitSet.length(); index++) {
			assertEquals(bitSetBean.bitSet.get(index), bitArray[index]);
		}
		
		bitSetBean = json.fromJson("{\"bitSet\":[2,3,0]}", BitSetBean.class);
		for (int index = 0; index < bitSetBean.bitSet.length(); index++) {
			assertEquals(bitSetBean.bitSet.get(index), bitArray[index]);
		}
		
		bitSetBean = json.fromJson("{\"bitSet\":[10,3,0]}", BitSetBean.class);
		for (int index = 0; index < bitSetBean.bitSet.length(); index++) {
			assertEquals(bitSetBean.bitSet.get(index), bitArray[index]);
		}
		
		bitSetBean = json.fromJson("{\"bitSet\":[\"10\",3,0]}", BitSetBean.class);
		for (int index = 0; index < bitSetBean.bitSet.length(); index++) {
			assertEquals(bitSetBean.bitSet.get(index), bitArray[index]);
		}
		
		bitSetBean = json.fromJson("{\"bitSet\":[\"A\",3,0]}", BitSetBean.class);
		assertEquals(bitSetBean.bitSet, null);
	}
	
	@Test
	public void testBooleanAdapter() throws IOException {
		BooleanBean booleanBean;
		booleanBean = json.fromJson("{\"booleanValue\":true}", BooleanBean.class);
		assertEquals(booleanBean.booleanValue, true);
		
		booleanBean = json.fromJson("{\"booleanValue\":\"true\"}", BooleanBean.class);
		assertEquals(booleanBean.booleanValue, true);
		
		booleanBean = json.fromJson("{\"booleanValue\":\"trued\"}", BooleanBean.class);
		assertEquals(booleanBean.booleanValue, false);
	}
	
	@Test
	public void testByteAdapter() throws IOException {
		ByteBean byteBean;
		byteBean = json.fromJson("{\"byteValue\":127}", ByteBean.class);
		assertEquals(byteBean.byteValue, 127);
		
		byteBean = json.fromJson("{\"byteValue\":\"127\"}", ByteBean.class);
		assertEquals(byteBean.byteValue, 127);
		
		byteBean = json.fromJson("{\"byteValue\":128}", ByteBean.class);
		assertEquals(byteBean.byteValue, -128);
		
		byteBean = json.fromJson("{\"byteValue\":-129}", ByteBean.class);
		assertEquals(byteBean.byteValue, 127);
		
		byteBean = json.fromJson("{\"byteValue\":\"\"}", ByteBean.class);
		assertEquals(byteBean.byteValue, 0);
	}
	
	@Test
	public void testCalendarAdapter() throws IOException {
		// TODO CalendarAdapter
	}
	
	@Test
	public void testCharacterAdapter() throws IOException {
		CharacterBean characterBean;
		characterBean = json.fromJson("{\"charValue\":\"A\"}", CharacterBean.class);
		assertEquals(characterBean.charValue, 'A');
		
		characterBean = json.fromJson("{\"charValue\":A}", CharacterBean.class);
		assertEquals(characterBean.charValue, 'A');
		
		characterBean = json.fromJson("{\"charValue\":65}", CharacterBean.class);
		assertEquals(characterBean.charValue, 0);
		
		characterBean = json.fromJson("{\"charValue\":\"AA\"}", CharacterBean.class);
		assertEquals(characterBean.charValue, 0);
	}
	
	@Test
	public void testCurrencyAdapter() throws IOException {
		// TODO CurrencyAdapter
	}
	
	@Test
	public void testDoubleAdapter() throws IOException {
		DoubleBean doubleBean;
		doubleBean = json.fromJson("{\"doubleValue\":12331221.123}", DoubleBean.class);
		assertEquals(doubleBean.doubleValue, 12331221.123, 0);
		
		doubleBean = json.fromJson("{\"doubleValue\":\"12331221.123\"}", DoubleBean.class);
		assertEquals(doubleBean.doubleValue, 12331221.123, 0);
		
		doubleBean = json.fromJson("{\"doubleValue\":\"A\"}", DoubleBean.class);
		assertEquals(doubleBean.doubleValue, 0, 0);
	}
	
	@Test
	public void testFloatAdapter() throws IOException {
		FloatBean floatBean;
		floatBean = json.fromJson("{\"floatValue\":123.123}", FloatBean.class);
		assertEquals(floatBean.floatValue, 123.123F, 0);
		
		floatBean = json.fromJson("{\"floatValue\":\"123.123\"}", FloatBean.class);
		assertEquals(floatBean.floatValue, 123.123F, 0);
		
		floatBean = json.fromJson("{\"floatValue\":\"A\"}", FloatBean.class);
		assertEquals(floatBean.floatValue, 0F, 0);
	}
	
	@Test
	public void testInetAddressAdapter() throws IOException {
		// TODO InetAddressAdapter
	}
	
	@Test
	public void testIntegerAdapter() throws IOException {
		IntegerBean integerBean;
		integerBean = json.fromJson("{\"intValue\":123}", IntegerBean.class);
		assertEquals(integerBean.intValue, 123);
		
		integerBean = json.fromJson("{\"intValue\":\"123\"}", IntegerBean.class);
		assertEquals(integerBean.intValue, 123);
		
		integerBean = json.fromJson("{\"intValue\":123.1}", IntegerBean.class);
		assertEquals(integerBean.intValue, 0);
	}
	
	@Test
	public void testLocaleAdapter() throws IOException {
		// TODO LocaleAdapter
	}
	
	@Test
	public void testLongAdapter() throws IOException {
		LongBean longBean;
		longBean = json.fromJson("{\"longValue\":123123123}", LongBean.class);
		assertEquals(longBean.longValue, 123123123);
		
		longBean = json.fromJson("{\"longValue\":\"123123123\"}", LongBean.class);
		assertEquals(longBean.longValue, 123123123);
		
		longBean = json.fromJson("{\"longValue\":\"123123123.123\"}", LongBean.class);
		assertEquals(longBean.longValue, 0);
	}
	
	@Test
	public void testNumberAdapter() throws IOException {
		NumberBean numberBean;
		numberBean = json.fromJson("{\"numberValue\":123}", NumberBean.class);
		assertEquals(numberBean.numberValue.intValue(), 123);
		
		numberBean = json.fromJson("{\"numberValue\":\"123\"}", NumberBean.class);
		assertEquals(numberBean.numberValue.intValue(), 123);
		
		numberBean = json.fromJson("{\"numberValue\":123.123}", NumberBean.class);
		assertEquals(numberBean.numberValue.doubleValue(), 123.123, 0);
		
		numberBean = json.fromJson("{\"numberValue\":\"123.123\"}", NumberBean.class);
		assertEquals(numberBean.numberValue.doubleValue(), 123.123, 0);
		
		try {
			numberBean = json.fromJson("{\"numberValue\":\"ABC\"}", NumberBean.class);
			numberBean.numberValue.doubleValue();
			fail();
		} catch (Exception ignored) {
		}
	}
	
	@Test
	public void testShortAdapter() throws IOException {
		ShortBean shortBean;
		shortBean = json.fromJson("{\"shortValue\":123}", ShortBean.class);
		assertEquals(shortBean.shortValue, 123);
		
		shortBean = json.fromJson("{\"shortValue\":\"123\"}", ShortBean.class);
		assertEquals(shortBean.shortValue, 123);
		
		shortBean = json.fromJson("{\"shortValue\":123.123}", ShortBean.class);
		assertEquals(shortBean.shortValue, 0);
		
		shortBean = json.fromJson("{\"shortValue\":\"ABC\"}", ShortBean.class);
		assertEquals(shortBean.shortValue, 0);
	}
	
	@Test
	public void testStringAdapter() throws IOException {
		StringBean stringBean;
		stringBean = json.fromJson("{\"stringValue\":\"abc\"}", StringBean.class);
		assertEquals(stringBean.stringValue, "abc");
		
		stringBean = json.fromJson("{\"stringValue\":abc}", StringBean.class);
		assertEquals(stringBean.stringValue, "abc");
		
		stringBean = json.fromJson("{\"stringValue\":123}", StringBean.class);
		assertEquals(stringBean.stringValue, "123");
		
		stringBean = json.fromJson("{\"stringValue\":null}", StringBean.class);
		assertEquals(stringBean.stringValue, null);
		
		stringBean = json.fromJson("{\"stringValue\":\"null\"}", StringBean.class);
		assertEquals(stringBean.stringValue, "null");
		
		stringBean = json.fromJson("{\"stringValue\":true}", StringBean.class);
		assertEquals(stringBean.stringValue, "true");
		
		stringBean = json.fromJson("{\"stringValue\":{\"value\":\"value\"}}", StringBean.class);
		assertEquals(stringBean.stringValue, "{\"value\":\"value\"}");
		
		stringBean = json.fromJson("{\"stringValue\":[1,2,3]}", StringBean.class);
		assertEquals(stringBean.stringValue, "[1,2,3]");
		
		// TODO 修改调整JsonReader，字符串输出时，不去除这些数据
		// stringBean = json.fromJson("{\"stringValue\":[\n\t1,\n\t2,\n\t3\n]}", StringBean.class);
		// assertEquals(stringBean.stringValue, "[\n\t1,\n\t2,\n\t3\n]");
	}
	
	@Test
	public void testStringBuilderAdapter() throws IOException {
		StringBuilderBean stringBuilderBean;
		stringBuilderBean = json.fromJson("{\"stringBuilderValue\":\"abc\"}", StringBuilderBean.class);
		assertEquals(stringBuilderBean.stringBuilderValue.toString(), "abc");
		
		stringBuilderBean = json.fromJson("{\"stringBuilderValue\":abc}", StringBuilderBean.class);
		assertEquals(stringBuilderBean.stringBuilderValue.toString(), "abc");
		
		stringBuilderBean = json.fromJson("{\"stringBuilderValue\":123}", StringBuilderBean.class);
		assertEquals(stringBuilderBean.stringBuilderValue.toString(), "123");
		
		stringBuilderBean = json.fromJson("{\"stringBuilderValue\":null}", StringBuilderBean.class);
		assertEquals(stringBuilderBean.stringBuilderValue, null);
		
		stringBuilderBean = json.fromJson("{\"stringBuilderValue\":\"null\"}", StringBuilderBean.class);
		assertEquals(stringBuilderBean.stringBuilderValue.toString(), "null");
		
		stringBuilderBean = json.fromJson("{\"stringBuilderValue\":true}", StringBuilderBean.class);
		assertEquals(stringBuilderBean.stringBuilderValue.toString(), "true");
		
		stringBuilderBean = json.fromJson("{\"stringBuilderValue\":{\"value\":\"value\"}}", StringBuilderBean.class);
		assertEquals(stringBuilderBean.stringBuilderValue.toString(), "{\"value\":\"value\"}");
		
		stringBuilderBean = json.fromJson("{\"stringBuilderValue\":[1,2,3]}", StringBuilderBean.class);
		assertEquals(stringBuilderBean.stringBuilderValue.toString(), "[1,2,3]");
	}
	
	@Test
	public void testStringBufferAdapter() throws IOException {
		StringBufferBean stringBufferBean;
		stringBufferBean = json.fromJson("{\"stringBufferValue\":\"abc\"}", StringBufferBean.class);
		assertEquals(stringBufferBean.stringBufferValue.toString(), "abc");
		
		stringBufferBean = json.fromJson("{\"stringBufferValue\":abc}", StringBufferBean.class);
		assertEquals(stringBufferBean.stringBufferValue.toString(), "abc");
		
		stringBufferBean = json.fromJson("{\"stringBufferValue\":123}", StringBufferBean.class);
		assertEquals(stringBufferBean.stringBufferValue.toString(), "123");
		
		stringBufferBean = json.fromJson("{\"stringBufferValue\":null}", StringBufferBean.class);
		assertEquals(stringBufferBean.stringBufferValue, null);
		
		stringBufferBean = json.fromJson("{\"stringBufferValue\":\"null\"}", StringBufferBean.class);
		assertEquals(stringBufferBean.stringBufferValue.toString(), "null");
		
		stringBufferBean = json.fromJson("{\"stringBufferValue\":true}", StringBufferBean.class);
		assertEquals(stringBufferBean.stringBufferValue.toString(), "true");
		
		stringBufferBean = json.fromJson("{\"stringBufferValue\":{\"value\":\"value\"}}", StringBufferBean.class);
		assertEquals(stringBufferBean.stringBufferValue.toString(), "{\"value\":\"value\"}");
		
		stringBufferBean = json.fromJson("{\"stringBufferValue\":[1,2,3]}", StringBufferBean.class);
		assertEquals(stringBufferBean.stringBufferValue.toString(), "[1,2,3]");
	}
	
	@Test
	public void testURIAdapter() throws IOException {
		URIBean uriBean;
		uriBean = json.fromJson("{\"uriValue\":\"http://www.url.com:9000/public/manuals/appliances?query#value\"}", URIBean.class);
		assertEquals(uriBean.uriValue.getAuthority(), "www.url.com:9000");
		assertEquals(uriBean.uriValue.getScheme(), "http");
		assertEquals(uriBean.uriValue.getHost(), "www.url.com");
		assertEquals(uriBean.uriValue.getPort(), 9000);
		assertEquals(uriBean.uriValue.getPath(), "/public/manuals/appliances");
		assertEquals(uriBean.uriValue.getQuery(), "query");
		assertEquals(uriBean.uriValue.getFragment(), "value");
		
		uriBean = json.fromJson("{\"uriValue\":false}", URIBean.class);
		assertEquals(uriBean.uriValue, null);
	}
	
	@Test
	public void testURLAdapter() throws IOException {
		URLBean urlBean;
		urlBean = json.fromJson("{\"urlValue\":\"http://host:1/path?query#fragment\"}", URLBean.class);
		assertEquals(urlBean.urlValue.getAuthority(), "host:1");
		assertEquals(urlBean.urlValue.getProtocol(), "http");
		assertEquals(urlBean.urlValue.getHost(), "host");
		assertEquals(urlBean.urlValue.getPort(), 1);
		assertEquals(urlBean.urlValue.getPath(), "/path");
		assertEquals(urlBean.urlValue.getQuery(), "query");
		
		urlBean = json.fromJson("{\"urlValue\":\"http\"}", URLBean.class);
		assertEquals(urlBean.urlValue, null);
	}
	
	@Test
	public void testUUIDAdapter() throws IOException {
		// TODO UUIDAdapter
	}
	
	@Test
	public void testArrayStringAdapter() throws IOException {
		ArrayStringBean arrayStringBean;
		arrayStringBean = json.fromJson("{\"arrayStringValue\":[\"1\",\"2\",\"3\"]}", ArrayStringBean.class);
		assertTrue(Arrays.equals(arrayStringBean.arrayStringValue, new String[]{"1", "2", "3"}));
		
		arrayStringBean = json.fromJson("{\"arrayStringValue\":[1,2,3]}", ArrayStringBean.class);
		assertTrue(Arrays.equals(arrayStringBean.arrayStringValue, new String[]{"1", "2", "3"}));
		
		arrayStringBean = json.fromJson("{\"arrayStringValue\":[1,2,3]}", ArrayStringBean.class);
		assertTrue(Arrays.equals(arrayStringBean.arrayStringValue, new String[]{"1", "2", "3"}));
		
		arrayStringBean = json.fromJson("{\"arrayStringValue\":{\"1\":\"1\"}}", ArrayStringBean.class);
		assertTrue(Arrays.equals(arrayStringBean.arrayStringValue, null));
	}
	
	@Test
	public void testArrayIntegerAdapter() throws IOException {
		ArrayIntegerBean arrayIntegerBean;
		arrayIntegerBean = json.fromJson("{\"arrayIntegerValue\":[1,2,3]}", ArrayIntegerBean.class);
		assertTrue(Arrays.equals(arrayIntegerBean.arrayIntegerValue, new int[]{1, 2, 3}));
		
		arrayIntegerBean = json.fromJson("{\"arrayIntegerValue\":[\"1\",\"2\",\"3\"]}", ArrayIntegerBean.class);
		assertTrue(Arrays.equals(arrayIntegerBean.arrayIntegerValue, new int[]{1, 2, 3}));
		
		try {
			json.fromJson("{\"arrayIntegerValue\":[\"1A\",\"2\",\"3\"]}", ArrayIntegerBean.class);
			fail();
		} catch (Exception ignored) {
		}
	}
	
	@Test
	public void testCollectionTypeAdapter() throws IOException {
		CollectionStringBean collectionStringBean;
		String[] resultStringArray;
		
		collectionStringBean = json.fromJson("{\"collectionValue\":[\"1\",\"2\",\"3\"]}", CollectionStringBean.class);
		resultStringArray = new String[]{"1", "2", "3"};
		for (int index = 0; index < collectionStringBean.collectionValue.size(); index++) {
			if (!collectionStringBean.collectionValue.get(index).equals(resultStringArray[index])) {
				fail();
			}
		}
		
		collectionStringBean = json.fromJson("{\"collectionValue\":[1,2,3]}", CollectionStringBean.class);
		resultStringArray = new String[]{"1", "2", "3"};
		for (int index = 0; index < collectionStringBean.collectionValue.size(); index++) {
			if (!collectionStringBean.collectionValue.get(index).equals(resultStringArray[index])) {
				fail();
			}
		}
		
		CollectionIntegerBean collectionIntegerBean;
		Integer[] resultIntegerArray;
		
		collectionIntegerBean = json.fromJson("{\"collectionValue\":[\"1\",\"2\",\"3\"]}", CollectionIntegerBean.class);
		resultIntegerArray = new Integer[]{1, 2, 3};
		for (int index = 0; index < collectionIntegerBean.collectionValue.size(); index++) {
			if (!collectionIntegerBean.collectionValue.get(index).equals(resultIntegerArray[index])) {
				fail();
			}
		}
		
		collectionIntegerBean = json.fromJson("{\"collectionValue\":[1,2,3]}", CollectionIntegerBean.class);
		resultIntegerArray = new Integer[]{1, 2, 3};
		for (int index = 0; index < collectionIntegerBean.collectionValue.size(); index++) {
			if (!collectionIntegerBean.collectionValue.get(index).equals(resultIntegerArray[index])) {
				fail();
			}
		}
		
		try {
			json.fromJson("{\"collectionValue\":[\"1A\",\"2\",\"3\"]}", CollectionIntegerBean.class);
			fail();
		} catch (Exception ignored) {
		}
	}
	
	@Test
	public void testEnumTypeAdapter() throws IOException {
		EnumBean enumBean;
		enumBean = json.fromJson("{\"enumValue\":\"VALUE1\"}", EnumBean.class);
		assertEquals(enumBean.enumValue, EnumValue.VALUE1);
		
		enumBean = json.fromJson("{\"enumValue\":VALUE1}", EnumBean.class);
		assertEquals(enumBean.enumValue, EnumValue.VALUE1);
		
		enumBean = json.fromJson("{\"enumValue\":\"false\"}", EnumBean.class);
		assertEquals(enumBean.enumValue, null);
	}
	
	@Test
	public void testJsonElementAdapter() throws IOException {
		JsonElement jsonElement;
		
		jsonElement = json.fromJson("{}", JsonElement.class);
		assertEquals(jsonElement.getClass(), JsonObject.class);
		
		jsonElement = json.fromJson("[]", JsonElement.class);
		assertEquals(jsonElement.getClass(), JsonArray.class);
		
		jsonElement = json.fromJson("null", JsonElement.class);
		assertEquals(jsonElement, JsonNull.INSTANCE);
		
		jsonElement = json.fromJson("A", JsonElement.class);
		assertEquals(jsonElement.getAsString(), "A");
		
		jsonElement = json.fromJson("\"A\"", JsonElement.class);
		assertEquals(jsonElement.getAsString(), "A");
		
		jsonElement = json.fromJson("123", JsonElement.class);
		assertEquals(jsonElement.getAsInt(), 123);
		
		jsonElement = json.fromJson("123", JsonElement.class);
		assertEquals(jsonElement.getAsString(), "123");
		
		try {
			json.fromJson("", JsonElement.class);
			fail();
		} catch (JsonException ignored) {
		}
	}
	
	@Test
	public void testMapTypeAdapter() throws IOException {
		MapBean mapBean;
		
		mapBean = json.fromJson("{\"mapStringValue\":{\"key\":\"value\"}}", MapBean.class);
		assertEquals(mapBean.mapStringValue.get("key"), "value");
		
		mapBean = json.fromJson("{\"mapStringValue\":{\"key\":value}}", MapBean.class);
		assertEquals(mapBean.mapStringValue.get("key"), "value");
		
		mapBean = json.fromJson("{\"mapBooleanValue\":{true:value}}", MapBean.class);
		assertEquals(mapBean.mapBooleanValue.get(true), "value");
		
		mapBean = json.fromJson("{\"mapBooleanValue\":{\"true\":value}}", MapBean.class);
		assertEquals(mapBean.mapBooleanValue.get(true), "value");
	}
	
	@Test
	public void testObjectTypeAdapter() throws IOException {
		Object listValue = json.fromJson("[]", Object.class);
		assertEquals(listValue.getClass(), ArrayList.class);
	}
	
	@Test
	public void testJsonAdapter() throws IOException {
		AdapterBean adapterBean = json.fromJson("{\"stringValue\":\"must null\"}", AdapterBean.class);
		assertEquals(adapterBean.stringValue, null);
	}
}
