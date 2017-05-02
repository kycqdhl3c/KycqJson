package com.kycq.library.json.bean;

import com.kycq.library.json.annotation.JsonAdapter;

public class AdapterBean {
	@JsonAdapter(TestAdapter.class)
	public String stringValue;
}
