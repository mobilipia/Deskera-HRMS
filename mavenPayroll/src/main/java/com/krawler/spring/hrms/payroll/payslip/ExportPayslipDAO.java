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
package com.krawler.spring.hrms.payroll.payslip;

import java.util.Date;
import java.util.List;

import masterDB.Historydetail;
import masterDB.Payhistory;;

public interface ExportPayslipDAO {
	public List<Payhistory> getPayhistory(String userid, Date startDate, Date endDate);
	public List<Historydetail> getHistorydetail(String historyid, String name);
	public List<Historydetail> getHistorydetail(String historyid, String name, String type);
	public List<Historydetail> getHistorydetailNotType(String historyid, String name, String type);
	public List<Historydetail> getHistorydetail(String userid, Date startDate, Date endDate, String type);
}
