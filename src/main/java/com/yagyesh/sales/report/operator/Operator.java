package com.yagyesh.sales.report.operator;

import com.yagyesh.sales.dao.Dao;

public abstract class Operator<k> {
	
	protected Dao<k> dao;

	public Operator(Dao<k> dao) {
		this.dao = dao;
	}
	public Operator() {
		
	}
}
