package cn.koolcloud.ipos.appstore.entity;

import java.util.List;

public class Pay {

	private int type;
	private List<PayMents> list;
	
	public Pay() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Pay(int type, List<PayMents> list) {
		super();
		this.type = type;
		this.list = list;
	}

	public int getType() {
		return type;
	}
	 
	public void setType(int type) {
		this.type = type;
	}
	
	public List<PayMents> getList() {
		return list;
	}
	 
	public void setList(List<PayMents> list) {
		this.list = list;
	}
	
	
}
