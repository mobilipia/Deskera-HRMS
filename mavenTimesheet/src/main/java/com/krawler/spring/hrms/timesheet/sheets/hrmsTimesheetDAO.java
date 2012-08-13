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
package com.krawler.spring.hrms.timesheet.sheets;

import com.krawler.common.service.ServiceException;
import com.krawler.common.update.Updates;
import com.krawler.spring.common.KwlReturnObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.krawler.hrms.timesheet.Timesheet;
import com.krawler.hrms.timesheet.TimesheetTimer;

public interface hrmsTimesheetDAO {
    
    public KwlReturnObject AllTimesheets(HashMap<String, Object> requestParams ) throws ServiceException;

    public KwlReturnObject timesheetByUserID(HashMap<String, Object> requestParams) throws ServiceException;

    public KwlReturnObject insertTimeSheet(HashMap<String, Object> requestParams) throws ServiceException;

    public KwlReturnObject deletetimesheetjobs(HashMap<String, Object> requestParams) throws ServiceException;

    public KwlReturnObject AlltimesheetsApproval(HashMap<String, Object> requestParams) throws ServiceException;
    
    public KwlReturnObject timesheetsApprovalUpdates(HashMap<String, Object> requestParams);
    
    public KwlReturnObject timesheetsSubmittedUpdates(HashMap<String, Object> requestParams);
    
    public TimesheetTimer setTimer(HashMap<String, Object> requestParams);
    
    public TimesheetTimer getTimer(String userid);
    
    public List<Timesheet> getTimesheet(String userid, Date startdate, Date enddate, String jobname);
    
    public boolean saveTimesheet(Timesheet timesheet);
    
    public boolean deleteTimesheet(TimesheetTimer timer);
    
    public List <Updates> timerUpdates(TimesheetTimer timer, SimpleDateFormat sdf, Locale locale);
}
