package com.kycq.library.json;

import com.kycq.library.json.bean.BooleanBean;
import com.kycq.library.json.bean.ChildBean;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

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
	
	@Test
	public void testToJson() throws IOException {
		ArrayList<BooleanBean> list = new ArrayList<>();
		BooleanBean booleanBean = new BooleanBean();
		booleanBean.booleanValue = false;
		list.add(booleanBean);
		System.out.println("----");
		System.out.println(json.toJson(list));
	}
}
