/*
 * Copyright (C) 2012  Krawler Information Systems Pvt Ltd
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package com.krawler.hrms.common;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.krawler.common.admin.Useraccount;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import masterDB.PayrollHistory;

public class HrmsCommonPayroll {

	public int getTotalNumberOfWorkingDays(int frequency, Date date){
		int workingDays = 0;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int daysInMonth = calendar.getActualMaximum(Calendar.DATE);
		int currentDay = calendar.get(Calendar.DATE);
		try{
			if(frequency==0){//Monthly
				workingDays = daysInMonth;
        	}else if(frequency==1){//Weekly
        		workingDays = 7;
        	} else if(frequency==2){//Twice in Month
        		if(currentDay<=15){
        			workingDays = 15;
        		}else{
        			workingDays = daysInMonth-15;
        		}
        	}
		}catch(Exception e){
			e.printStackTrace();
			Logger.getLogger(HrmsCommonPayroll.class.getName()).log(Level.SEVERE, null, e);
		}
		return workingDays;
	}

	
	public Date getFinanacialYearStartDate(Date date, int month){
		try{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			if(calendar.get(Calendar.MONTH)<month){
				calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)-1);
			}
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DATE, 01);
			date= calendar.getTime();
		}catch(Exception e){
			e.printStackTrace();
		}
        return date;
    }

    public Date getFinanacialYearEndDate(Date date, int month){
    	try{
    		Calendar calendar = Calendar.getInstance();
    		calendar.setTime(date);
    		calendar.add(Calendar.MONTH, month);
			calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
			date= calendar.getTime();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        return date;
    }
    
    public int getMonthFromDate(Date date){
    
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        
        return calendar.get(Calendar.MONTH);
    
    }
    
    public int getRemainingWorkingMonth(Date startDate, Date endDate){

        int remainingMonths=0;
        
        int endMonth = getMonthFromDate(endDate); 
        int startMonth = getMonthFromDate(startDate); 
        
        if(startMonth <= endMonth){
        
            remainingMonths = 12-endMonth + startMonth - 1;
        } else {
        
            remainingMonths = startMonth - 1 - endMonth;
        
        }
        
       return remainingMonths;
        
        
    }
    
    public double getPaidTax(List<PayrollHistory> list, PayrollHistory payrollHistory){
    	double paidTax = 0;
    	try{
    		if(list!=null&&list.size()>0){
	    		for(PayrollHistory history : list){
	    			if(history.getPaycycleenddate().compareTo(payrollHistory.getPaycycleenddate())<=0){
	    				paidTax+=history.getIncometaxAmount();
	    			}
	    		}
    		}else{
    			paidTax = payrollHistory.getIncometaxAmount();
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return paidTax;
    }
    
    
    public String getEmployeeIdFormat(Useraccount ua, profileHandlerDAO profileHandlerDAO){
    	String employeeID = "";
    	try{
    		HashMap<String,Object> requestParams = new HashMap<String,Object>();
        	requestParams.put("companyid", ua.getUser().getCompany().getCompanyID());
        	requestParams.put("empid", ua.getEmployeeid());
        	if(ua.getEmployeeIdFormat()==null){
            	employeeID = String.valueOf(ua.getEmployeeid() == null ? "" : profileHandlerDAO.getEmpidFormatEdit(requestParams).getEntityList().get(0));
            }else{
            	requestParams.put("standardEmpId", profileHandlerDAO.getEmpidFormatEdit(requestParams).getEntityList().get(0).toString());
            	requestParams.put("employeeIdFormat", ua.getEmployeeIdFormat());
                employeeID = profileHandlerDAO.getNewEmployeeIdFormat(requestParams);
            }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return employeeID;
    }
}
