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

package com.krawler.spring.hrms.employee;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.common.admin.userSalaryTemplateMap;
import com.krawler.spring.common.KwlReturnObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import masterDB.Payhistory;

/**
 *
 * @author shs
 */
public interface hrmsEmpDAO {

public KwlReturnObject getCurrencyDetails(HashMap<String, Object> requestParams);
public KwlReturnObject getPayHistory(HashMap<String, Object> requestParams);
	public List<Payhistory> getPayHistorys(String userids, Date startDate, Date endDate);
public KwlReturnObject setPayHistory(HashMap<String, Object> requestParams);
public KwlReturnObject setHistorydetail(HashMap<String, Object> requestParams);
public KwlReturnObject deleteHistorydetail(HashMap<String, Object> requestParams);
public KwlReturnObject deleteEmpPayHistory(HashMap<String, Object> requestParams);

public KwlReturnObject getHistoryDetail(HashMap<String, Object> requestParams);
public KwlReturnObject salaryGeneratedUpdates(HashMap<String, Object> requestParams);
public Company getCompany(Map<String, Object> requestParams);
public List<Payhistory> getSalariesBetweenStartAndEndDate(Map<String, Object> requestParams);
public List<userSalaryTemplateMap> getUserSalaryTemplateMap(Map<String, Object> requestParams);
public KwlReturnObject generateApprovedSalary(String[] historyIDs, int mode);
public User getPayHistoryEmployeeName(String historyid);
}
