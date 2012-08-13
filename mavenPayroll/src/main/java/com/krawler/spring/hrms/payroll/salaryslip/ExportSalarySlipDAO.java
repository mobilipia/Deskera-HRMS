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
package com.krawler.spring.hrms.payroll.salaryslip;

import java.util.Date;
import java.util.List;
import java.util.Map;

import masterDB.ComponentResourceMappingHistory;
import masterDB.PayrollHistory;

import com.krawler.common.admin.User;
import com.krawler.common.admin.Useraccount;
import com.krawler.hrms.ess.Empprofile;

public interface ExportSalarySlipDAO {
	public User getUser(String userid);
	public Empprofile getEmpprofile(String userid);
	public PayrollHistory getPayrollHistory(String id);
	public Useraccount getUserAccount(String id);
	public List<String> getHeaders(Date enddate, String companyId, String amountText);
	public List<User> getUsersList(String frequency, Date enddate, String companyId, String module, Integer status);
	public List<String> getComponentIds(Date enddate, int frequency, String companyId);
	public Map<String, ComponentResourceMappingHistory> getSalaryHistoryForUser(Date enddate, int frequency, String userid);
}
