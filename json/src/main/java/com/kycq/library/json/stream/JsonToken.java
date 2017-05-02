package com.kycq.library.json.stream;

public enum JsonToken {
	/** 数组开头 */
	BEGIN_ARRAY,
	/** 数组结尾 */
	END_ARRAY,
	/** 对象开头 */
	BEGIN_OBJECT,
	/** 对象结尾 */
	END_OBJECT,
	/** 键 */
	NAME,
	/** 字符串类型 */
	STRING,
	/** 数字类型 */
	NUMBER,
	/** 布尔类型 */
	BOOLEAN,
	/** 空值 */
	NULL,
	/** 无类型 */
	NONE,
	/** 文档结尾 */
	END_DOCUMENT
}
