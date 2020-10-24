package com.yagyesh.sales.report.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.yagyesh.sales.dao.Dao;
import com.yagyesh.sales.report.ReportOperation;

public class ReportOperator extends Operator<ReportOperation>{
			
	public ReportOperator(Dao<ReportOperation> dao) {
		super(dao);
	}
	
	public void printDbCallsForDuration(Long fromDate, Long toDate ) throws Exception {
		List<ReportOperation> Reports = getReport(fromDate, toDate);
		dao.printDBCalls(Reports);
	}
	
	private List<ReportOperation> getReport(Long fromDate, Long toDate) throws Exception {
		validateCorrectData(fromDate,toDate);
		List<ReportOperation> reports = new ArrayList<ReportOperation>();
		DateTime from = new DateTime(fromDate);
		DateTime to = new DateTime(toDate);	
		
		System.out.println("Requested Data for :"+from.getDayOfMonth() + " "+ from.monthOfYear().getAsShortText()
				 + " to "+ to.getDayOfMonth()+ " "+to.monthOfYear().getAsShortText() );
		
		int fromDay = from.getDayOfMonth();
		int fromMonth = from.getMonthOfYear();
		int toDay = to.getDayOfMonth();
		int toMonth = to.getMonthOfYear();
		int year = to.getYear();		
		
		if(toMonth == fromMonth && toDay == fromDay) {
			ReportOperation op = createAndGet(year, fromMonth,fromMonth, fromDay, fromDay, ReportOperation.GET, ReportOperation.DAILY);
			reports.add(op);
		}
		else if(toMonth == fromMonth+1 && toDay - fromDay <0 && toDay < 7 && (fromDay > 29 || fromMonth == 2 && fromDay > 22)) {
			ReportOperation op = createAndGet(year, fromMonth,toMonth, fromDay, toDay, ReportOperation.SUM, ReportOperation.DAILY);
			reports.add(op);
		}
		else {
			Map<Integer,ReportOperation> monthsToGet = getMonthyReport(from,to);
			for(int i=fromMonth; i<=toMonth;i++) {
				if(monthsToGet.containsKey(i)) {
					reports.add(monthsToGet.get(i));
					continue;
				}
				int fromDayForMonth = (i == fromMonth)? fromDay: 1;
				int toDayForMonth = (i == toMonth)? toDay:from.dayOfMonth().getMaximumValue();
				
				DateTime currentFrom = new DateTime(year,i,fromDayForMonth,0,0); 
				DateTime currentTo = new DateTime(year,i,toDayForMonth,0,0); 

				List<ReportOperation> weeklyReport = getWeeklyandDailyReportForMonth(currentFrom, currentTo);
				reports.addAll(weeklyReport);			
			}
		}		
		return reports;
	}
	
private List<ReportOperation> getWeeklyandDailyReportForMonth(DateTime from, DateTime to) {
		
		List<ReportOperation> weeklyandDaily = new ArrayList<ReportOperation>(); 
		
		int fromDay = from.getDayOfMonth();
		int toDay = to.getDayOfMonth();
		int year = to.getYear();
		int preInterval = nextInterval(fromDay, from);
		int lastInterval = prevInterval(toDay, to);
		int month = to.getMonthOfYear();
		int maxDays = to.dayOfMonth().getMaximumValue();
		if(fromDay == toDay) {
			ReportOperation op = createAndGet(year, month,month, fromDay, fromDay, ReportOperation.GET, ReportOperation.DAILY);
			weeklyandDaily.add(op);
		}
		else if(toDay - fromDay <= 11 && (nextInterval(toDay, to)-1 > toDay) && (prevInterval(fromDay, from)+1 < fromDay)) {
			ReportOperation op = createAndGet(year, month,month, fromDay, toDay, ReportOperation.SUM, ReportOperation.DAILY);
			weeklyandDaily.add(op);
		}
		else if(toDay - fromDay <= 6 && (((nextInterval(toDay, to) >= toDay) && (prevInterval(fromDay, from)+1 < fromDay)) || (prevInterval(fromDay, from) <= fromDay && (nextInterval(toDay, to)-1 > toDay)))) {
			ReportOperation op = createAndGet(year, month,month, fromDay, toDay, ReportOperation.SUM, ReportOperation.DAILY);
			weeklyandDaily.add(op);
		}
	    else {
			
			if(preInterval - fromDay < 7) {
				if(preInterval > toDay) {
					preInterval = toDay+1;
				}
				ReportOperation op = null;
				if(fromDay == preInterval -1) {
					op = createAndGet(year, month,month, fromDay, preInterval-1, ReportOperation.GET, ReportOperation.DAILY);
				}
				else {
					op = createAndGet(year, month,month, fromDay, preInterval-1, ReportOperation.SUM, ReportOperation.DAILY);
				}
				weeklyandDaily.add(op);
				fromDay = preInterval;
			}
			int prev = fromDay;
			for(int i = fromDay; i <= lastInterval ; i=i+7) {
				ReportOperation op = createAndGet(year, month,month, i, i+6, ReportOperation.GET, ReportOperation.WEEKLY);
				weeklyandDaily.add(op);
				prev = i+7;
			}	
			if(prev >= 29 && toDay >= prev && toDay == maxDays) {
				ReportOperation op = createAndGet(year, month,month, prev, toDay, ReportOperation.GET, ReportOperation.WEEKLY);
				weeklyandDaily.add(op);
			}
			else if(prev == toDay) {
				ReportOperation op = createAndGet(year, month,month, prev, toDay, ReportOperation.GET, ReportOperation.DAILY);
				weeklyandDaily.add(op);
			}
			else if(prev <= toDay){
				ReportOperation op = createAndGet(year, month,month, prev, toDay, ReportOperation.SUM, ReportOperation.DAILY);
				weeklyandDaily.add(op);
			}
		}
		
		
		return weeklyandDaily;
	}
	
	private int nextInterval(int fromDay, DateTime from) {
		int nextInterval =-1;
		
		if(fromDay >= 1 && fromDay <=7) {
			nextInterval = 8;
		}
		
		else if(fromDay >= 8 && fromDay <=14) {
			nextInterval = 15;
		}
		
		else if( fromDay >=15 && fromDay <=21) {
			nextInterval = 22;
		}
		else if( fromDay >=22 && fromDay <=28) {
			nextInterval = 29;
		}
		else {
			nextInterval = from.dayOfMonth().getMaximumValue();
		}
		return nextInterval;
		
	}
	
	private int prevInterval(int toDay, DateTime to) {
		int prevInterval =-1;
		if(toDay >= 1 && toDay <7) {
			prevInterval = 0;
		}
		
		else if(toDay >= 7 && toDay <14) {
			prevInterval = 7;
		}
		
		else if( toDay >=14 && toDay <21) {
			prevInterval = 14;
		}
		else if( toDay >=21 && toDay <28) {
			prevInterval = 21;
		}
		else {
			prevInterval = 28;
		}
		return prevInterval;
	}

	private Map<Integer,ReportOperation> getMonthyReport(DateTime from, DateTime to) {
		int fromDay = from.getDayOfMonth();
		int fromMonth = from.getMonthOfYear();
		int toDay = to.getDayOfMonth();
		int toMonth = to.getMonthOfYear();
		int year = from.getYear();
		int i=0;
		int months = toMonth - fromMonth;
		if(fromDay != 1) {
			 i=1;
			 from = from.plusMonths(1);
		}
		if(to.dayOfMonth().getMaximumValue() != toDay) {
			months = months -1;
		}
		Map<Integer,ReportOperation> monthMap = new HashMap<Integer,ReportOperation>();
		
		//This is for getting consecutive months which can get complete.
		DateTime currentMonth = from;
		for(; i<= months;i++) {
			ReportOperation Report = createAndGet(year, currentMonth.getMonthOfYear(),currentMonth.getMonthOfYear(), 1, currentMonth.dayOfMonth().getMaximumValue(), ReportOperation.GET, ReportOperation.MONTHLY);
			monthMap.put(fromMonth+i, Report);
			currentMonth = currentMonth.plusMonths(1);
		}
		return monthMap;
		
	}
	
	private ReportOperation createAndGet(int year, int month1, int month2, int firstDay, int lastDay, String type, String interval) {
		DateTime localFrom = new DateTime(year, month1, firstDay, 0, 0);
		DateTime localTo = new DateTime(year, month2, lastDay, 0, 0);
		ReportOperation report = new ReportOperation(type,localFrom,localTo,interval);
		return report;
	}

	private void validateCorrectData(Long fromDate, Long toDate) throws Exception {
		if(fromDate == null || toDate == null) {
			throw new Exception("Can't print Data for null inputs. ");
		}
		if(fromDate > toDate) {
			throw new Exception("From Date Cannot be greater than to date!");
		}
	}
}
