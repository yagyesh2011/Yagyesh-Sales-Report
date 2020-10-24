package com.yagyesh.sales.report;

import org.joda.time.DateTime;

public class ReportOperation {

	public static final String GET = "Get";
	public static final String SUM = "Sum";
	public static final String DAILY = "Daily";
	public static final String WEEKLY = "Weekly";
	public static final String MONTHLY = "Monthly";
	private String operation;
	private DateTime from;
	private DateTime to;
	public String interval;
	
	public ReportOperation(String operation, DateTime from, DateTime to, String interval) {
		super();
		this.operation = operation;
		this.from = from;
		this.to = to;
		this.interval = interval;
	}
	
	@Override
	public String toString() {
		return  "Operation - "+operation+ ", From - "+from.getDayOfMonth()+ " "+ from.monthOfYear().getAsShortText()+ ", To - "+to.getDayOfMonth()+" "+to.monthOfYear().getAsShortText()+ ", interval - "+interval;
	}
}
