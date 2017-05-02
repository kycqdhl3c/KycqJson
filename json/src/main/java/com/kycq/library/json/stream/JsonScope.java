package com.kycq.library.json.stream;

class JsonScope {
	/** 不存在值的数组，在关闭前不需要分隔符或换行符 */
	public static final int EMPTY_ARRAY = 1;
	/** 存在至少有一个值的数组，在获取下个元素前需要逗号和换行符 */
	public static final int NONEMPTY_ARRAY = 2;
	/** 不存在键值对的对象，在关闭前不需要分隔符或换行符 */
	public static final int EMPTY_OBJECT = 3;
	/** 已读取键值对的键，下个元素必须为键值对的值 */
	public static final int DANGLING_NAME = 4;
	/** 至少存在一个键值对对象，在读取下一个元素钱需要逗号和换行符 */
	public static final int NONEMPTY_OBJECT = 5;
	/** 无相关文件读取 */
	public static final int EMPTY_DOCUMENT = 6;
	/** 开始读取相关的文件 */
	public static final int NONEMPTY_DOCUMENT = 7;
	/** 文件关闭并无法读取 */
	public static final int CLOSED = 8;
}
