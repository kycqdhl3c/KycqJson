package com.kycq.library.json.stream;

import com.kycq.library.json.JsonException;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;

public class JsonWriter implements Flushable, Closeable {
	private static final String[] REPLACEMENT_CHARS;
	private static final String[] HTML_SAFE_REPLACEMENT_CHARS;
	
	static {
		REPLACEMENT_CHARS = new String[128];
		for (int i = 0; i <= 0x1f; i++) {
			REPLACEMENT_CHARS[i] = String.format("\\u%04x", (int) i);
		}
		REPLACEMENT_CHARS['"'] = "\\\"";
		REPLACEMENT_CHARS['\\'] = "\\\\";
		REPLACEMENT_CHARS['\t'] = "\\t";
		REPLACEMENT_CHARS['\b'] = "\\b";
		REPLACEMENT_CHARS['\n'] = "\\n";
		REPLACEMENT_CHARS['\r'] = "\\r";
		REPLACEMENT_CHARS['\f'] = "\\f";
		HTML_SAFE_REPLACEMENT_CHARS = REPLACEMENT_CHARS.clone();
		HTML_SAFE_REPLACEMENT_CHARS['<'] = "\\u003c";
		HTML_SAFE_REPLACEMENT_CHARS['>'] = "\\u003e";
		HTML_SAFE_REPLACEMENT_CHARS['&'] = "\\u0026";
		HTML_SAFE_REPLACEMENT_CHARS['='] = "\\u003d";
		HTML_SAFE_REPLACEMENT_CHARS['\''] = "\\u0027";
	}
	
	/** 键值分割符 */
	private static final String SEPARATOR = ":";
	
	/** JSON数据写入流 */
	private final Writer mWriter;
	
	/** 解析栈 */
	private int[] mScopeStack = new int[32];
	/** 栈大小 */
	private int mStackSize = 0;
	
	{
		// 初始栈顶数据
		push(JsonScope.EMPTY_DOCUMENT);
	}
	
	/** 当前写入键 */
	private String mWriteName;
	
	private boolean isReformat = true;
	
	/**
	 * 构造方法
	 *
	 * @param writer JSON数据写入流
	 */
	public JsonWriter(Writer writer) {
		if (writer == null) {
			throw new NullPointerException("writer can't be null");
		}
		mWriter = writer;
	}
	
	/**
	 * 构造方法
	 *
	 * @param writer     JSON数据写入流
	 * @param isReformat 是否格式化
	 */
	public JsonWriter(Writer writer, boolean isReformat) {
		if (writer == null) {
			throw new NullPointerException("writer can't be null");
		}
		mWriter = writer;
		this.isReformat = isReformat;
	}
	
	/**
	 * 开始写入数组
	 *
	 * @throws JsonException 错误信息
	 */
	public JsonWriter beginArray() throws JsonException {
		writeName();
		beforeValue();
		push(JsonScope.EMPTY_ARRAY);
		write("[");
		return this;
	}
	
	/**
	 * 结束写入数组
	 *
	 * @throws JsonException 错误信息
	 */
	public JsonWriter endArray() throws JsonException {
		int peekStack = peek();
		if (peekStack != JsonScope.EMPTY_ARRAY && peekStack != JsonScope.NONEMPTY_ARRAY) {
			throw new JsonException("Expected EMPTY_ARRAY or NONEMPTY_ARRAY but was " + peekStack);
		}
		if (mWriteName != null) {
			throw new IllegalStateException("dirty name: " + mWriteName);
		}
		
		mStackSize--;
		if (peekStack == JsonScope.NONEMPTY_ARRAY) {
			newline();
		}
		write("]");
		return this;
	}
	
	/**
	 * 开始写入对象
	 *
	 * @throws JsonException 错误信息
	 */
	public JsonWriter beginObject() throws JsonException {
		writeName();
		beforeValue();
		push(JsonScope.EMPTY_OBJECT);
		write("{");
		return this;
	}
	
	/**
	 * 结束写入对象
	 *
	 * @throws JsonException 错误信息
	 */
	public JsonWriter endObject() throws JsonException {
		int peekStack = peek();
		if (peekStack != JsonScope.EMPTY_OBJECT && peekStack != JsonScope.NONEMPTY_OBJECT) {
			throw new JsonException("Expected EMPTY_OBJECT or NONEMPTY_OBJECT but was " + peekStack);
		}
		if (mWriteName != null) {
			throw new JsonException("dirty name: " + mWriteName);
		}
		
		mStackSize--;
		if (peekStack == JsonScope.NONEMPTY_OBJECT) {
			newline();
		}
		write("}");
		return this;
	}
	
	/**
	 * 写入键名
	 *
	 * @param name 键名
	 * @throws JsonException 错误信息
	 */
	public JsonWriter name(String name) throws JsonException {
		if (name == null) {
			throw new NullPointerException("name can't be null");
		}
		if (mWriteName != null) {
			throw new JsonException("name " + mWriteName + " already exist");
		}
		if (mStackSize == 0) {
			throw new IllegalStateException("JsonWriter is closed.");
		}
		mWriteName = name;
		return this;
	}
	
	/**
	 * 写入空值
	 *
	 * @throws JsonException 错误信息
	 */
	public JsonWriter valueNull() throws JsonException {
		writeName();
		beforeValue();
		write("null");
		return this;
	}
	
	/**
	 * 写入值
	 *
	 * @param value 值
	 * @throws JsonException 错误信息
	 */
	public JsonWriter value(String value) throws JsonException {
		if (value == null) {
			return valueNull();
		}
		writeName();
		beforeValue();
		safeWrite(value);
		return this;
	}
	
	/**
	 * 写入值
	 *
	 * @param value 值
	 * @throws JsonException 错误信息
	 */
	public JsonWriter value(boolean value) throws JsonException {
		writeName();
		beforeValue();
		write(value ? "true" : "false");
		return this;
	}
	
	/**
	 * 写入值
	 *
	 * @param value 值
	 * @throws JsonException 错误信息
	 */
	public JsonWriter value(Boolean value) throws JsonException {
		if (value == null) {
			return valueNull();
		}
		writeName();
		beforeValue();
		write(value ? "true" : "false");
		return this;
	}
	
	/**
	 * 写入值
	 *
	 * @param value 值
	 * @throws JsonException 错误信息
	 */
	public JsonWriter value(double value) throws JsonException {
		if (Double.isNaN(value) || Double.isInfinite(value)) {
			throw new JsonException("Numeric values must be finite, but was " + value);
		}
		writeName();
		beforeValue();
		write(Double.toString(value));
		return this;
	}
	
	/**
	 * 写入值
	 *
	 * @param value 值
	 * @throws JsonException 错误信息
	 */
	public JsonWriter value(long value) throws JsonException {
		writeName();
		beforeValue();
		write(Long.toString(value));
		return this;
	}
	
	/**
	 * 写入值
	 *
	 * @param value 值
	 * @throws JsonException 错误信息
	 */
	public JsonWriter value(Number value) throws JsonException {
		if (value == null) {
			return valueNull();
		}
		writeName();
		beforeValue();
		write(value.toString());
		return this;
	}
	
	/**
	 * 输出键名
	 *
	 * @throws JsonException 错误信息
	 */
	private void writeName() throws JsonException {
		if (mWriteName == null) {
			return;
		}
		
		int peekStack = mScopeStack[mStackSize - 1];
		if (peekStack == JsonScope.NONEMPTY_OBJECT) {
			write(",");
		} else if (peekStack != JsonScope.EMPTY_OBJECT) {
			throw new IllegalStateException("Nesting problem.");
		}
		newline();
		replaceTop(JsonScope.DANGLING_NAME);
		safeWrite(mWriteName);
		mWriteName = null;
	}
	
	private void beforeValue() throws JsonException {
		switch (mScopeStack[mStackSize - 1]) {
			case JsonScope.NONEMPTY_DOCUMENT:
			case JsonScope.EMPTY_DOCUMENT:
				replaceTop(JsonScope.NONEMPTY_DOCUMENT);
				break;
			case JsonScope.EMPTY_ARRAY:
				replaceTop(JsonScope.NONEMPTY_ARRAY);
				newline();
				break;
			case JsonScope.NONEMPTY_ARRAY:
				write(",");
				newline();
				break;
			case JsonScope.DANGLING_NAME:
				write(SEPARATOR);
				replaceTop(JsonScope.NONEMPTY_OBJECT);
				break;
			default:
				throw new IllegalStateException("Nesting problem.");
		}
	}
	
	/**
	 * 转换字符串为兼容类型输出
	 *
	 * @param value 字符串值
	 * @throws JsonException 错误信息
	 */
	private void safeWrite(String value) throws JsonException {
		String[] replacements = HTML_SAFE_REPLACEMENT_CHARS;
		write("\"");
		int last = 0;
		int length = value.length();
		for (int i = 0; i < length; i++) {
			char c = value.charAt(i);
			String replacement;
			if (c < 128) {
				replacement = replacements[c];
				if (replacement == null) {
					continue;
				}
			} else if (c == '\u2028') {
				replacement = "\\u2028";
			} else if (c == '\u2029') {
				replacement = "\\u2029";
			} else {
				continue;
			}
			if (last < i) {
				write(value, last, i - last);
			}
			write(replacement);
			last = i + 1;
		}
		if (last < length) {
			write(value, last, length - last);
		}
		write("\"");
	}
	
	private void newline() throws JsonException {
		if (!isReformat) {
			return;
		}
		write("\n");
		for (int index = 1, size = mStackSize; index < size; index++) {
			write("  ");
		}
	}
	
	/**
	 * 输出字符串
	 *
	 * @param str 字符串
	 * @throws JsonException 错误信息
	 */
	private void write(String str) throws JsonException {
		try {
			mWriter.write(str);
		} catch (IOException e) {
			throw new JsonException(e);
		}
	}
	
	/**
	 * 输出字符串
	 *
	 * @param str 字符串
	 * @param off 偏移位置
	 * @param len 输出长度
	 * @throws JsonException 错误信息
	 */
	private void write(String str, int off, int len) throws JsonException {
		try {
			mWriter.write(str, off, len);
		} catch (IOException e) {
			throw new JsonException(e);
		}
	}
	
	private void push(int newTop) {
		if (mStackSize == mScopeStack.length) {
			int[] newStack = new int[mStackSize * 2];
			System.arraycopy(mScopeStack, 0, newStack, 0, mStackSize);
			mScopeStack = newStack;
		}
		mScopeStack[mStackSize++] = newTop;
	}
	
	private int peek() {
		if (mStackSize == 0) {
			throw new IllegalStateException("JsonWriter is closed.");
		}
		return mScopeStack[mStackSize - 1];
	}
	
	private void replaceTop(int topOfStack) {
		mScopeStack[mStackSize - 1] = topOfStack;
	}
	
	@Override
	public void flush() throws IOException {
		if (mStackSize == 0) {
			throw new IllegalStateException("JsonWriter is closed.");
		}
		mWriter.flush();
	}
	
	@Override
	public void close() throws IOException {
		mWriter.close();
		
		int size = mStackSize;
		if (size > 1 || size == 1 && mScopeStack[size - 1] != JsonScope.NONEMPTY_DOCUMENT) {
			throw new IOException("Incomplete document");
		}
		mStackSize = 0;
	}
}
