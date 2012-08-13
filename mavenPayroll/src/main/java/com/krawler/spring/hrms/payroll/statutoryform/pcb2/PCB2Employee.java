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

package com.krawler.spring.hrms.payroll.statutoryform.pcb2;

/**
 *
 * @author krawler
 */
public class PCB2Employee {

    private String incomeResit;
	private String resitDate;
	private String amountPCB;
	private String incomeYear;
	private String incomeMonth;
	private String incomeType;

    public String getAmountPCB() {
        return amountPCB;
    }

    public void setAmountPCB(String amountPCB) {
        this.amountPCB = amountPCB;
    }

    public String getIncomeMonth() {
        return incomeMonth;
    }

    public void setIncomeMonth(String incomeMonth) {
        this.incomeMonth = incomeMonth;
    }

    public String getIncomeResit() {
        return incomeResit;
    }

    public void setIncomeResit(String incomeResit) {
        this.incomeResit = incomeResit;
    }

    public String getIncomeType() {
        return incomeType;
    }

    public void setIncomeType(String incomeType) {
        this.incomeType = incomeType;
    }

    public String getIncomeYear() {
        return incomeYear;
    }

    public void setIncomeYear(String incomeYear) {
        this.incomeYear = incomeYear;
    }

    public String getResitDate() {
        return resitDate;
    }

    public void setResitDate(String resitDate) {
        this.resitDate = resitDate;
    }

    

}
