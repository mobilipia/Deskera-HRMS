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

public class HrmsPayrollConstants {

	public static final int PAYROLL_HISTORY_STATUS_ENTERED = 1;
	public static final int PAYROLL_HISTORY_STATUS_CALCULATE = 2;
	public static final int PAYROLL_HISTORY_STATUS_AUTHORIZED = 3;
	public static final int PAYROLL_HISTORY_STATUS_UNAUTHORIZED = 4;
	public static final int PAYROLL_HISTORY_STATUS_PROCESSED_TRIAL = 5;
	public static final int PAYROLL_HISTORY_STATUS_PROCESSED_FINAL = 6;
	
	public static final int PAYROLL_COMPONENT_TYPE_EMPLOYER_CONTRIBUTION = 0;
	public static final int PAYROLL_COMPONENT_TYPE_EARNING = 1;
	public static final int PAYROLL_COMPONENT_TYPE_DEDUCTION = 2;
	public static final int PAYROLL_COMPONENT_TYPE_TAX = 3;
	public static final int PAYROLL_COMPONENT_TYPE_OTHER_REMUNERATION = 4;
	public static final int PAYROLL_COMPONENT_TYPE_INCOMETAX = 5;
}
