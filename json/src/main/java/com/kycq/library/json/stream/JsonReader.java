package com.kycq.library.json.stream;

import com.kycq.library.json.JsonException;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;

public final class JsonReader implements Closeable {
	/** 开始读取数字 */
	private static final int NUMBER_NONE = 0;
	/** 读取到负号 */
	private static final int NUMBER_SIGN = 1;
	/** 读取到数字 */
	private static final int NUMBER_DIGIT = 2;
	/** 读取到小数点 */
	private static final int NUMBER_DOT = 3;
	/** 读取到分数的数字 */
	private static final int NUMBER_FRACTION_DIGIT = 4;
	/** 读取到指数标志E或e */
	private static final int NUMBER_EXP_E = 5;
	/** 读取到跟随在指数标志后的-+号 */
	private static final int NUMBER_EXP_SIGN = 6;
	/** 读取到指数后面的数字 */
	private static final int NUMBER_EXP_DIGIT = 7;
	
	/** JSON字符串读取流 */
	private final Reader mReader;
	/** JSON字符串读取位置 */
	private int mJsonPosition = 0;
	
	/** JSON字符串缓存区 */
	private char[] mBuffer = new char[1024];
	/** 缓存区开始位置 */
	private int mBufferPosition = 0;
	/** 缓存区结束位置 */
	private int mBufferLimit = 0;
	
	/** 栈大小 */
	private int mStackSize = 0;
	/** 解析栈 */
	private int[] mScopeStack = new int[32];
	/** 路径名称 */
	private String[] mPathNames = new String[32];
	/** 路径数组指数 */
	private int[] mPathIndices = new int[32];
	
	{
		// 初始栈顶数据
		mScopeStack[mStackSize++] = JsonScope.EMPTY_DOCUMENT;
	}
	
	/** 预存值 */
	private String mNextValue;
	/** 当前解析的值类型 */
	private JsonToken mJsonToken = JsonToken.NONE;
	
	/**
	 * 构造方法
	 *
	 * @param jsonStr JSON字符串数据
	 */
	public JsonReader(String jsonStr) {
		this(new StringReader(jsonStr));
	}
	
	/**
	 * 构造方法
	 *
	 * @param reader JSON数据读取流
	 */
	public JsonReader(Reader reader) {
		if (reader == null) {
			throw new NullPointerException("reader can't be null");
		}
		mReader = reader;
	}
	
	/**
	 * 开始读取数组
	 *
	 * @throws JsonException 错误信息
	 */
	public void beginArray() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}
		
		if (token == JsonToken.BEGIN_ARRAY) {
			push(JsonScope.EMPTY_ARRAY);
			mPathIndices[mStackSize - 1] = 0;
			mJsonToken = JsonToken.NONE;
		} else {
			throw syntaxException("Expected BEGIN_ARRAY but was " + next(), mNextValue);
		}
	}
	
	/**
	 * 结束读取数组
	 *
	 * @throws JsonException 错误信息
	 */
	public void endArray() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}
		
		if (token == JsonToken.END_ARRAY) {
			mStackSize--;
			mPathIndices[mStackSize - 1]++;
			mJsonToken = JsonToken.NONE;
		} else {
			throw syntaxException("Expected END_ARRAY but was " + next(), mNextValue);
		}
	}
	
	/**
	 * 开始读取对象
	 *
	 * @throws JsonException 错误信息
	 */
	public void beginObject() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}
		
		if (token == JsonToken.BEGIN_OBJECT) {
			push(JsonScope.EMPTY_OBJECT);
			mJsonToken = JsonToken.NONE;
		} else {
			throw syntaxException("Expected BEGIN_OBJECT but was " + next(), mNextValue);
		}
	}
	
	/**
	 * 结束读取对象
	 *
	 * @throws JsonException 错误信息
	 */
	public void endObject() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}
		
		if (token == JsonToken.END_OBJECT) {
			mStackSize--;
			mPathNames[mStackSize] = null;
			mPathIndices[mStackSize - 1]++;
			mJsonToken = JsonToken.NONE;
		} else {
			throw syntaxException("Expected END_OBJECT but was " + next(), mNextValue);
		}
	}
	
	/**
	 * 是否有值读取
	 *
	 * @return true 是
	 * <p>
	 * false 否
	 * @throws JsonException 错误信息
	 */
	public boolean hasNext() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}
		
		return token != JsonToken.END_OBJECT && token != JsonToken.END_ARRAY;
	}
	
	/**
	 * 读取键名
	 *
	 * @return 键名
	 * @throws JsonException 错误信息
	 */
	public String nextName() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}
		
		String result;
		if (token == JsonToken.NAME) {
			result = mNextValue;
		} else {
			throw syntaxException("Expected a name but was " + next(), mNextValue);
		}
		
		mJsonToken = JsonToken.NONE;
		mPathNames[mStackSize - 1] = result;
		return result;
	}
	
	public void nameToValue() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}
		
		if (token == JsonToken.NAME) {
			mJsonToken = JsonToken.STRING;
		} else {
			throw syntaxException("Expected a name but was " + next(), mNextValue);
		}
	}
	
	/**
	 * 读取字符串
	 *
	 * @return 字符串
	 * @throws JsonException 错误信息
	 */
	public String nextString() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}
		
		String result;
		if (token == JsonToken.STRING || token == JsonToken.NUMBER || token == JsonToken.BOOLEAN) {
			result = mNextValue;
		} else {
			throw syntaxException("Expected a string but was " + next(), mNextValue);
		}
		
		mNextValue = null;
		mJsonToken = JsonToken.NONE;
		mPathIndices[mStackSize - 1]++;
		return result;
	}
	
	/**
	 * 读取空值
	 *
	 * @throws JsonException 错误信息
	 */
	public void nextNull() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}
		
		if (token == JsonToken.NULL) {
			mNextValue = null;
			mJsonToken = JsonToken.NONE;
			mPathIndices[mStackSize - 1]++;
		} else {
			throw syntaxException("Expected null but was " + next(), mNextValue);
		}
	}
	
	/**
	 * 读取Boolean值
	 *
	 * @return Boolean值
	 * @throws JsonException 错误信息
	 */
	public boolean nextBoolean() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}
		
		Boolean result;
		if (token == JsonToken.BOOLEAN) {
			result = Boolean.parseBoolean(mNextValue);
		} else {
			throw syntaxException("Expected a boolean but was " + next(), mNextValue);
		}
		
		mNextValue = null;
		mJsonToken = JsonToken.NONE;
		mPathIndices[mStackSize - 1]++;
		return result;
	}
	
	/**
	 * 读取Double值
	 *
	 * @return Double值
	 * @throws JsonException 错误信息
	 */
	public double nextDouble() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}
		
		double result;
		if (token == JsonToken.NUMBER || token == JsonToken.STRING) {
			result = new BigDecimal(mNextValue).doubleValue();
		} else {
			throw syntaxException("Expected a double but was " + next(), mNextValue);
		}
		
		// 判断是否非法
		if ((Double.isNaN(result) || Double.isInfinite(result))) {
			throw syntaxException("Json forbids NaN and infinities: " + result, mNextValue);
		}
		
		mNextValue = null;
		mJsonToken = JsonToken.NONE;
		mPathIndices[mStackSize - 1]++;
		return result;
	}
	
	/**
	 * 读取Float值
	 *
	 * @return Float值
	 * @throws JsonException 错误信息
	 */
	public float nextFloat() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}
		
		double asDouble;
		if (token == JsonToken.NUMBER || token == JsonToken.STRING) {
			asDouble = Double.parseDouble(mNextValue);
		} else {
			throw syntaxException("Expected a float but was " + next(), mNextValue);
		}
		
		float result = (float) asDouble;
		
		mNextValue = null;
		mJsonToken = JsonToken.NONE;
		mPathIndices[mStackSize - 1]++;
		return result;
	}
	
	/**
	 * 读取Long值
	 *
	 * @return Long值
	 * @throws JsonException 错误信息
	 */
	public long nextLong() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}
		
		double asDouble;
		if (token == JsonToken.NUMBER || token == JsonToken.STRING) {
			asDouble = Double.parseDouble(mNextValue);
		} else {
			throw syntaxException("Expected a long but was " + next(), mNextValue);
		}
		
		long result = (long) asDouble;
		if (result != asDouble) { // 确认是否精度丢失
			throw syntaxException("Expected an long but was " + next(), mNextValue);
		}
		
		mNextValue = null;
		mJsonToken = JsonToken.NONE;
		mPathIndices[mStackSize - 1]++;
		return result;
	}
	
	/**
	 * 读取Integer值
	 *
	 * @return Integer值
	 * @throws JsonException 错误信息
	 */
	public int nextInt() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}
		
		double asDouble;
		if (token == JsonToken.NUMBER || token == JsonToken.STRING) {
			asDouble = Double.parseDouble(mNextValue);
		} else {
			throw syntaxException("Expected a int but was " + next(), mNextValue);
		}
		
		int result = (int) asDouble;
		if (result != asDouble) { // 确认是否精度丢失
			throw syntaxException("Expected a int but was " + next(), mNextValue);
		}
		
		mNextValue = null;
		mJsonToken = JsonToken.NONE;
		mPathIndices[mStackSize - 1]++;
		return result;
	}
	
	/**
	 * 略过该值的读取
	 *
	 * @throws JsonException 错误信息
	 */
	public void skipValue() throws JsonException {
		int count = 0;
		do {
			JsonToken token = mJsonToken;
			if (token == JsonToken.NONE) {
				token = doPeek();
			}
			
			if (token == JsonToken.BEGIN_ARRAY) {
				push(JsonScope.EMPTY_ARRAY);
				count++;
			} else if (token == JsonToken.BEGIN_OBJECT) {
				push(JsonScope.EMPTY_OBJECT);
				count++;
			} else if (token == JsonToken.END_ARRAY) {
				mStackSize--;
				count--;
			} else if (token == JsonToken.END_OBJECT) {
				mStackSize--;
				count--;
			} else if (token == JsonToken.STRING || token == JsonToken.NUMBER) {
				mNextValue = null;
			}
			
			mJsonToken = JsonToken.NONE;
		} while (count != 0);
		
		mPathNames[mStackSize - 1] = "null";
		mPathIndices[mStackSize - 1]++;
	}
	
	/**
	 * 读取下个类型
	 *
	 * @return 值类型
	 * @throws JsonException 错误信息
	 */
	public JsonToken next() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}
		
		switch (token) {
			case NONE:
				throw new JsonException("NONE");
			default:
				return token;
		}
	}
	
	/**
	 * 读取值
	 *
	 * @return 值类型
	 * @throws JsonException 错误信息
	 */
	public JsonToken doPeek() throws JsonException {
		int peekStack = mScopeStack[mStackSize - 1];
		if (peekStack == JsonScope.EMPTY_DOCUMENT) {
			// TODO 过滤不规范的开头
			// consumeNonExecutePrefix();
			// 标志开始读取流内容
			mScopeStack[mStackSize - 1] = JsonScope.NONEMPTY_DOCUMENT;
		} else if (peekStack == JsonScope.NONEMPTY_DOCUMENT) {
			int character = nextNonWhitespace(false);// 读取下个字符至数组缓存中
			if (character == -1) {// 流内容已读取结束
				return mJsonToken = JsonToken.END_DOCUMENT;
			} else {
				mJsonPosition--;
				mBufferPosition--;
			}
		} else if (peekStack == JsonScope.EMPTY_OBJECT || peekStack == JsonScope.NONEMPTY_OBJECT) {
			mScopeStack[mStackSize - 1] = JsonScope.DANGLING_NAME;
			
			// 在下个元素之前，查找到逗号
			if (peekStack == JsonScope.NONEMPTY_OBJECT) {
				int character = nextNonWhitespace(true);
				switch (character) {
					case '}':// 读取到对象结束标志
						return mJsonToken = JsonToken.END_OBJECT;
					case ';':
					case ',':// 下个对象之前必须的逗号
						break;
					default:
						throw syntaxException("Unterminated object", mNextValue);
				}
			}
			
			// 正式读取对象的第一个元素的键
			int character = nextNonWhitespace(true);
			switch (character) {
				case '"':// 正确的读取到键的双引号
					nextQuotedValue('"');
					return mJsonToken = JsonToken.NAME;
				case '\'':// 兼容版本的读取单引号
					nextQuotedValue('\'');
					return mJsonToken = JsonToken.NAME;
				case '}':// 对象结束
					if (peekStack != JsonScope.NONEMPTY_OBJECT) {
						return mJsonToken = JsonToken.END_OBJECT;
					} else {
						// 未读取到键
						throw syntaxException("Expected name", mNextValue);
					}
				default:// 读取到的是无引号的键
					mJsonPosition--;
					mBufferPosition--;
					if (isLiteral((char) character)) {// 确认读取到的不是非法字符
						nextUnquotedValue();
						return mJsonToken = JsonToken.NAME;
					} else {
						throw syntaxException("Expected name", mNextValue);
					}
			}
		} else if (peekStack == JsonScope.DANGLING_NAME) {
			mScopeStack[mStackSize - 1] = JsonScope.NONEMPTY_OBJECT;
			// 查找在键值之间的冒号:
			int character = nextNonWhitespace(true);
			switch (character) {
				case ':':// 正确获取
					break;
				case '=':// 兼容JSON格式
					break;
				default:
					throw syntaxException("Expected ':'", mNextValue);
			}
		} else if (peekStack == JsonScope.EMPTY_ARRAY) {
			mScopeStack[mStackSize - 1] = JsonScope.NONEMPTY_ARRAY;
		} else if (peekStack == JsonScope.NONEMPTY_ARRAY) {
			int character = nextNonWhitespace(true);
			switch (character) {
				case ']':
					return mJsonToken = JsonToken.END_ARRAY;
				case ';':
				case ',':
					break;
				default:
					throw syntaxException("Unterminated array", mNextValue);
			}
		} else if (peekStack == JsonScope.CLOSED) {
			throw new JsonException("JsonReader is closed");
		}
		
		int character = nextNonWhitespace(true);
		switch (character) {
			case '{':// 读取到对象开始标志
				return mJsonToken = JsonToken.BEGIN_OBJECT;
			case '[':// 读取到数组开始标志
				return mJsonToken = JsonToken.BEGIN_ARRAY;
			case ']':
				if (peekStack == JsonScope.EMPTY_ARRAY) {
					return mJsonToken = JsonToken.END_ARRAY;
				}
			case ';':
			case ',':
				if (peekStack == JsonScope.EMPTY_ARRAY || peekStack == JsonScope.NONEMPTY_ARRAY) {
					mJsonPosition--;
					mBufferPosition--;
					return mJsonToken = JsonToken.NULL;
				} else {
					throw syntaxException("Unexpected value", mNextValue);
				}
			case '\'':
				return mJsonToken = nextQuotedValue('\'');
			case '"':// 读取键值对的值
				return mJsonToken = nextQuotedValue('"');
			default:// 退回栈
				mJsonPosition--;
				mBufferPosition--;
		}
		
		JsonToken result = nextKeyWord();
		if (result != JsonToken.NONE) {
			return mJsonToken = result;
		}
		
		result = nextNumber();
		if (result != JsonToken.NONE) {
			return mJsonToken = result;
		}
		
		if (!isLiteral(mBuffer[mBufferPosition])) {
			throw syntaxException("Expected value", mNextValue);
		}
		
		return mJsonToken = nextUnquotedValue();
	}
	
	/**
	 * 返回下一个引号之前的字符串，不包括引号。
	 *
	 * @param quoted 引号
	 * @return 值类型
	 * @throws JsonException 错误信息
	 */
	private JsonToken nextQuotedValue(char quoted) throws JsonException {
		char[] buffer = mBuffer;
		StringBuilder builder = new StringBuilder();
		int hashCode = 0;
		while (true) {
			int bufferPosition = mBufferPosition;
			int bufferLimit = mBufferLimit;
			// 当前未添加到builder内的字符串的第一个字符位置
			int start = bufferPosition;
			while (bufferPosition < bufferLimit) {// 逐一匹配当前缓存区的字符
				int character = buffer[bufferPosition++];
				if (character == quoted) {// 匹配到对于的引号
					mJsonPosition += bufferPosition - mBufferPosition;
					mBufferPosition = bufferPosition;
					builder.append(buffer, start, bufferPosition - start - 1);
					mNextValue = builder.toString();
					return JsonToken.STRING;
				} else if (character == '\\') {// 转义字符
					mJsonPosition += bufferPosition - mBufferPosition;
					mBufferPosition = bufferPosition;
					builder.append(buffer, start, bufferPosition - start - 1);
					builder.append(readEscapeCharacter());
					
					bufferPosition = mBufferPosition;
					bufferLimit = mBufferLimit;
					start = bufferPosition;
				} else {// 其他字符
					hashCode = (hashCode * 31) + character;
				}
			}
			
			builder.append(buffer, start, bufferPosition - start);
			mJsonPosition += bufferPosition - mBufferPosition;
			mBufferPosition = bufferPosition;
			if (!fillBuffer(1)) {
				throw syntaxException("Unterminated string", builder.toString());
			}
		}
	}
	
	/**
	 * 读取一个不带引号的值，并作为字符串返回。
	 *
	 * @return 值类型
	 * @throws JsonException 错误信息
	 */
	private JsonToken nextUnquotedValue() throws JsonException {
		StringBuilder builder = null;
		int index = 0;
		
		while (true) {
			// 循环检查剩余的字符，判断是否遇到隔断字符
			for (; mBufferPosition + index < mBufferLimit; index++) {
				char character = mBuffer[mBufferPosition + index];
				if (character == '/'
						|| character == '\\'
						|| character == ';'
						|| character == '#'
						|| character == '='
						|| character == '{'
						|| character == '}'
						|| character == '['
						|| character == ']'
						|| character == ':'
						|| character == ','
						|| character == ' '
						|| character == '\t'
						|| character == '\f'
						|| character == '\n'
						|| character == '\r') {
					break;
				}
			}
			
			// 检查到隔断字符
			if (mBufferPosition + index < mBufferLimit) {
				break;
			}
			
			// 当把剩余的字符读取结束，但未检测到隔断字符时，需要调整数组缓存空间，
			// 使所需字符填充满数组缓存，达到最大利用缓存的目的。
			if (index < mBuffer.length) {
				if (fillBuffer(index + 1)) {
					continue;
				} else {
					break;
				}
			}
			
			// 当读取完整个数组缓存，并且未读取到隔断字符时，该值已过长，需要使用StringBuilder来进行缓存
			if (builder == null) {
				builder = new StringBuilder();
			}
			builder.append(mBuffer, mBufferPosition, index);
			mJsonPosition += index;
			mBufferPosition += index;
			index = 0;
			if (!fillBuffer(1)) {
				break;
			}
		}
		
		String result;
		if (builder == null) {
			result = new String(mBuffer, mBufferPosition, index);
		} else {
			builder.append(mBuffer, mBufferPosition, index);
			result = builder.toString();
		}
		
		mJsonPosition += index;
		mBufferPosition += index;
		mNextValue = result;
		return JsonToken.STRING;
	}
	
	/**
	 * 读取关键字
	 *
	 * @return 值类型
	 * @throws JsonException 错误信息
	 */
	private JsonToken nextKeyWord() throws JsonException {
		// 根据首个字符匹配关键字
		char character = mBuffer[mBufferPosition];
		String keyword;
		String keywordUpper;
		JsonToken token;
		if (character == 't' || character == 'T') {
			keyword = "true";
			keywordUpper = "TRUE";
			token = JsonToken.BOOLEAN;
		} else if (character == 'f' || character == 'F') {
			keyword = "false";
			keywordUpper = "FALSE";
			token = JsonToken.BOOLEAN;
		} else if (character == 'n' || character == 'N') {
			keyword = "null";
			keywordUpper = "NULL";
			token = JsonToken.NULL;
		} else {
			return JsonToken.NONE;
		}
		
		// 逐一匹配关键字的值[1..length]
		int length = keyword.length();
		for (int index = 1; index < length; index++) {
			if (mBufferPosition + index >= mBufferLimit && !fillBuffer(index + 1)) {
				return JsonToken.NONE;
			}
			
			character = mBuffer[mBufferPosition + index];
			
			if (character != keyword.charAt(index) && character != keywordUpper.charAt(index)) {
				return JsonToken.NONE;
			}
		}
		
		// 值读取到true，false，null就结束，完全符合
		if ((mBufferPosition + length < mBufferLimit || fillBuffer(length + 1)) && isLiteral(mBuffer[mBufferPosition + length])) {
			return JsonToken.NONE;
		}
		
		mNextValue = new String(mBuffer, mBufferPosition, length);
		mJsonPosition += length;
		mBufferPosition += length;
		return token;
	}
	
	/**
	 * 读取数字
	 *
	 * @return 值类型
	 * @throws JsonException 错误信息
	 */
	@SuppressWarnings("ConstantConditions")
	private JsonToken nextNumber() throws JsonException {
		char[] buffer = mBuffer;
		int bufferPosition = mBufferPosition;
		int bufferLimit = mBufferLimit;
		int index = 0;
		
		int last = NUMBER_NONE;
		long value = 0;
		
		for (; true; index++) {
			if (bufferPosition + index == bufferLimit) {
				if (!fillBuffer(index + 1)) {
					return JsonToken.NONE;
				}
				bufferPosition = mBufferPosition;
				bufferLimit = mBufferLimit;
			}
			
			char character = buffer[bufferPosition + index];
			if (character == '-') {
				if (last == NUMBER_NONE) {
					last = NUMBER_SIGN;
					continue;
				}
				if (last == NUMBER_EXP_E) {
					last = NUMBER_EXP_SIGN;
					continue;
				}
				return JsonToken.NONE;
			} else if (character == '+') {
				if (last == NUMBER_EXP_E) {
					last = NUMBER_EXP_SIGN;
					continue;
				}
				return JsonToken.NONE;
			} else if (character == '.') {
				if (last == NUMBER_DIGIT) {
					last = NUMBER_DOT;
					continue;
				}
				return JsonToken.NONE;
			} else if (character == 'e' || character == 'E') {
				if (last == NUMBER_FRACTION_DIGIT || last == NUMBER_DIGIT) {
					last = NUMBER_EXP_E;
					continue;
				}
				return JsonToken.NONE;
			} else {
				if (character < '0' || character > '9') {
					if (!isLiteral(character)) {
						break;
					}
					return JsonToken.NONE;
				}
				if (last == NUMBER_NONE || last == NUMBER_SIGN) {
					value = -(character - '0');
					last = NUMBER_DIGIT;
				} else if (last == NUMBER_DIGIT) {
					if (value == 0) {
						return JsonToken.NONE;
					}
					
					value = value * 10 - (character - '0');
				} else if (last == NUMBER_DOT) {
					last = NUMBER_FRACTION_DIGIT;
				} else if (last == NUMBER_EXP_E || last == NUMBER_EXP_SIGN) {
					last = NUMBER_EXP_DIGIT;
				}
			}
		}
		
		if (last == NUMBER_DIGIT || last == NUMBER_FRACTION_DIGIT || last == NUMBER_EXP_DIGIT) {
			mNextValue = new String(mBuffer, mBufferPosition, index);
			mJsonPosition += index;
			mBufferPosition += index;
			return JsonToken.NUMBER;
		} else {
			return JsonToken.NONE;
		}
	}
	
	/**
	 * 读取转义字符，支持读取Unicode转义符及\n、\r、\b、\f。
	 *
	 * @return 转义字符
	 * @throws JsonException 错误信息
	 */
	private char readEscapeCharacter() throws JsonException {
		if (mBufferPosition == mBufferLimit && !fillBuffer(1)) {
			throw syntaxException("Unterminated escape sequence", null);
		}
		
		char escaped = mBuffer[mBufferPosition++];
		switch (escaped) {
			case 'u':
				if (mBufferPosition + 4 > mBufferLimit && !fillBuffer(4)) {
					throw syntaxException("Unterminated escape sequence", new String(mBuffer, mBufferPosition, mBufferLimit - mBufferPosition));
				}
				
				// Unicode16进制转换
				char result = 0;
				for (int i = mBufferPosition, end = i + 4; i < end; i++) {
					char c = mBuffer[i];
					result <<= 4;
					if (c >= '0' && c <= '9') {
						result += c - '0';
					} else if (c >= 'a' && c <= 'f') {
						result += c - 'a' + 10;
					} else if (c >= 'A' && c <= 'F') {
						result += c - 'A' + 10;
					} else {
						throw syntaxException("Unicode transform error", "\\u" + new String(mBuffer, mBufferPosition, 4));
					}
				}
				mJsonPosition += 4;
				mBufferPosition += 4;
				return result;
			case 't':
				return '\t';
			case 'b':
				return '\b';
			case 'n':
				return '\n';
			case 'r':
				return '\r';
			case 'f':
				return '\f';
			case '\n':
			case '\'':
			case '"':
			case '\\':
			default:
				return escaped;
		}
	}
	
	/**
	 * 返回流中的下一个字符，该字符不能为空格或注释的一部分。返回后，该字符始终存储在数组缓存中的mPos-1的位置。
	 *
	 * @param throwOnEof 读取到结束是否抛出异常
	 * @return 字符
	 * @throws JsonException 错误信息
	 */
	private int nextNonWhitespace(boolean throwOnEof) throws JsonException {
		char[] buffer = mBuffer;
		int bufferPosition = mBufferPosition;
		int bufferLimit = mBufferLimit;
		
		while (true) {
			if (bufferPosition == bufferLimit) {// 数组缓存内数据已读取结束
				mJsonPosition += bufferPosition - mBufferPosition;
				mBufferPosition = bufferPosition;
				if (!fillBuffer(1)) {
					break;
				}
				bufferPosition = mBufferPosition;
				bufferLimit = mBufferLimit;
			}
			
			int character = buffer[bufferPosition++];
			if (character == '\n' || character == ' ' || character == '\r' || character == '\t') {// 读取到换行符、空格或制表符号
				continue;
			}
			
			if (character == '/') {// 过滤注解
				mJsonPosition += bufferPosition - mBufferPosition;
				mBufferPosition = bufferPosition;
				if (bufferPosition == bufferLimit) {// 读取后面的字符
					mJsonPosition--;
					mBufferPosition--;// 将'/'放置回数组缓存
					boolean charsLoaded = fillBuffer(2);
					mJsonPosition++;
					mBufferPosition++;// 重新读取'/'字符
					if (!charsLoaded) {// 后面没有字符，非注解，返回该字符
						return character;
					}
				}
				
				char peek = buffer[mBufferPosition];
				switch (peek) {
					case '*':// 多行注解
						mJsonPosition++;
						mBufferPosition++;// 跳过多行注解的开头'*'
						if (!skipTo("*/")) {// 跳转到多行注解的末尾
							throw syntaxException("Unterminated comment", null);
						}
						bufferPosition = mBufferPosition + 2;
						bufferLimit = mBufferLimit;
						continue;
					case '/':// 单行注解
						mJsonPosition++;
						mBufferPosition++;// 跳过第二个'/'
						skipToEndOfLine();// 跳过"//"后面一行的注解内容
						bufferPosition = mBufferPosition;
						bufferLimit = mBufferLimit;
						continue;
					default: // 非注解
						return character;
				}
			} else if (character == '#') {// 跳过#之后的哈希注解。JSON的RFC没有特别指出，但需要在解析中指出。http://b/2571423
				mJsonPosition += bufferPosition - mBufferPosition;
				mBufferPosition = bufferPosition;
				skipToEndOfLine();
				bufferPosition = mBufferPosition;
				bufferLimit = mBufferLimit;
			} else {// 读取到所需数据
				mJsonPosition += bufferPosition - mBufferPosition;
				mBufferPosition = bufferPosition;
				return character;
			}
		}
		
		if (throwOnEof) {
			throw syntaxException("End of input", null);
		} else {
			return -1;
		}
	}
	
	/**
	 * 是否普通字符
	 *
	 * @param c 字符
	 * @return true 为普通字符
	 */
	private boolean isLiteral(char c) {
		switch (c) {
			case '/':
			case '\\':
			case ';':
			case '#':
			case '=':
			case '{':
			case '}':
			case '[':
			case ']':
			case ':':
			case ',':
			case ' ':
			case '\t':
			case '\f':
			case '\r':
			case '\n':
				return false;
			default:
				return true;
		}
	}
	
	/**
	 * 过滤夹杂在JSON字符串中的多行注释
	 *
	 * @param toFind 过滤字符串
	 * @return true 过滤完成
	 * <p>
	 * false 过滤失败
	 * @throws JsonException 错误信息
	 */
	private boolean skipTo(String toFind) throws JsonException {
		while (mBufferPosition + toFind.length() <= mBufferLimit || fillBuffer(toFind.length())) {
			if (mBuffer[mBufferPosition] == '\n') {
				mJsonPosition++;
				mBufferPosition++;
				continue;
			}
			
			int index = 0;
			while (index < toFind.length()) {
				if (mBuffer[mBufferPosition + 1] != toFind.charAt(index)) {
					break;
				}
				index++;
			}
			
			if (index >= toFind.length()) {
				return true;
			} else {
				mJsonPosition++;
				mBufferPosition++;
			}
		}
		
		return false;
	}
	
	/**
	 * 读取字符，直到换行符。如果换行符由"\r\n"组成，需要由调用该方法的调用者自行消除\n。
	 *
	 * @throws JsonException 错误信息
	 */
	private void skipToEndOfLine() throws JsonException {
		while (mBufferPosition < mBufferLimit || fillBuffer(1)) {
			mJsonPosition++;
			char c = mBuffer[mBufferPosition++];
			if (c == '\n' || c == '\r') {
				break;
			}
		}
	}
	
	/**
	 * 读取缓存流至缓存区
	 *
	 * @param minByte 最少缓存字节
	 * @return true 缓存成功
	 * <p>
	 * false 缓存失败
	 * @throws JsonException 错误信息
	 */
	private boolean fillBuffer(int minByte) throws JsonException {
		char[] buffer = mBuffer;
		if (mBufferPosition != mBufferLimit) {// 将未读取的数据载入到缓存中
			mBufferLimit -= mBufferPosition;
			System.arraycopy(buffer, mBufferPosition, buffer, 0, mBufferLimit);
		} else {// 缓存中的数据已读取结束
			mBufferLimit = 0;
		}
		
		mBufferPosition = 0;
		try {
			int total;
			// 读取数据填充数组缓存的空位置
			while ((total = mReader.read(buffer, mBufferLimit, buffer.length - mBufferLimit)) != -1) {
				mBufferLimit += total;
				// 第一次读取，如果存在位元组顺序标志，则跳过
				if (mJsonPosition == 0 && mBufferLimit > 0 && buffer[0] == '\ufeff') {
					mBufferPosition++;
					mJsonPosition++;
					minByte++;
				}
				
				if (mBufferLimit >= minByte) {// 已读取缓存大于最低要求
					return true;
				}
			}
		} catch (IOException e) {
			throw new JsonException(e);
		}
		
		return false;
	}
	
	private void push(int newTop) {
		if (mStackSize == mScopeStack.length) {
			int[] newStack = new int[mStackSize * 2];
			System.arraycopy(mScopeStack, 0, newStack, 0, mStackSize);
			mScopeStack = newStack;
			
			String[] newPathNames = new String[mStackSize * 2];
			System.arraycopy(mPathNames, 0, newPathNames, 0, mStackSize);
			mPathNames = newPathNames;
			
			int[] newPathIndices = new int[mStackSize * 2];
			System.arraycopy(mPathIndices, 0, newPathIndices, 0, mStackSize);
			mPathIndices = newPathIndices;
		}
		mScopeStack[mStackSize++] = newTop;
	}
	
	/**
	 * 语法错误信息构建
	 *
	 * @param message 错误信息
	 * @param value   错误值
	 * @return 错误信息
	 */
	private JsonException syntaxException(String message, String value) {
		StringBuilder builder = new StringBuilder(message);
		if (value != null && value.length() != 0) {
			builder.append(" for ");
			builder.append(value);
		}
		builder.append(getCurrentPath());
		
		return new JsonException(builder.toString());
	}
	
	public String getCurrentPath() {
		StringBuilder result = new StringBuilder().append(" at jsonStr path: $");
		for (int index = 0, size = mStackSize; index < size; index++) {
			switch (mScopeStack[index]) {
				case JsonScope.EMPTY_ARRAY:
				case JsonScope.NONEMPTY_ARRAY:
					result.append('[').append(mPathIndices[index]).append(']');
					break;
				case JsonScope.EMPTY_OBJECT:
				case JsonScope.DANGLING_NAME:
				case JsonScope.NONEMPTY_OBJECT:
					result.append('.');
					if (mPathNames[index] != null) {
						result.append(mPathNames[index]);
					}
					break;
				case JsonScope.NONEMPTY_DOCUMENT:
				case JsonScope.EMPTY_DOCUMENT:
				case JsonScope.CLOSED:
					break;
			}
		}
		return result.toString();
	}
	
	@Override
	public void close() throws IOException {
		mJsonToken = JsonToken.NONE;
		mScopeStack[0] = JsonScope.CLOSED;
		mStackSize = 1;
		mReader.close();
	}
}
