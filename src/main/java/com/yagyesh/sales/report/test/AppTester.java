package com.yagyesh.sales.report.test;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.yagyesh.sales.dao.ReportDao;
import com.yagyesh.sales.report.operator.Operator;
import com.yagyesh.sales.report.operator.ReportOperator;


/**
 * Hello world!
 *Assumptions: 
 "The last week of a month would be "29th-31st" or "29th-30th", etc,
according to the month."
1. )Considering that last week data is also present in the DB.
2. )Db only contains data on day basis and not hourly basis.(Example doesn't show it and is not mentioned in the question)
3. )Considering Db will be queried for same year for from date and to date. As the example output doesn't prints the year. 
	So it doesn't state which year the db is queried for. 
 */
public class AppTester 
{
    public static void main( String[] args )
    {
    	DateTimeFormatter df =DateTimeFormat.forPattern("dd/MM/yyyy"); 
    	/*
    	 * Just Update the 2 dates with above format and test the results out.
    	 * *///failing 14/03 03/05 .
    	try {
    		long from = df.parseMillis("30/01/2020"); 
        	long to = df.parseMillis("07/03/2020"); 
        	ReportOperator op = new ReportOperator(new ReportDao());
			op.printDbCallsForDuration(from, to);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
