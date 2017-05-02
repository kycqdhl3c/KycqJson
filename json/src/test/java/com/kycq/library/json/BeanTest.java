package com.kycq.library.json;

import com.kycq.library.json.bean.ChildBean;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class BeanTest {
	Json json;
	
	@Before
	public void setUp() {
		json = new Json();
	}
	
	@Test
	public void testParentBean() throws IOException {
		ChildBean childBean;
		childBean = json.fromJson("{\"parentValue\":\"parent\",\"childValue\":\"child\"}", ChildBean.class);
		assertEquals(childBean.parentValue, "parent");
		assertEquals(childBean.childValue, "child");
	}
}
