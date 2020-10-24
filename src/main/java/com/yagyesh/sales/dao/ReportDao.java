package com.yagyesh.sales.dao;

import java.util.List;

import com.yagyesh.sales.report.ReportOperation;

public class ReportDao implements Dao<ReportOperation> {
	
	@Override
	public void printDBCalls(List<ReportOperation> m) {
		m.stream().forEach(System.out::println);
	}

}
