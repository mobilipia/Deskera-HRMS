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
package masterDB;

public class MalaysianUserTaxBenefits {
	
	private String id;
	private double paidZakat;
	private double paidEPF;
	private double paidLICAndOther;
	private double paidBIK;
	private PayrollHistory payrollHistory;
    private double paidOtherDeduction;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public double getPaidZakat() {
		return paidZakat;
	}
	public void setPaidZakat(double paidZakat) {
		this.paidZakat = paidZakat;
	}
	
	public double getPaidEPF() {
		return paidEPF;
	}
	public void setPaidEPF(double paidEPF) {
		this.paidEPF = paidEPF;
	}
	
	public double getPaidLICAndOther() {
		return paidLICAndOther;
	}
	public void setPaidLICAndOther(double paidLICAndOther) {
		this.paidLICAndOther = paidLICAndOther;
	}
	
	public double getPaidBIK() {
		return paidBIK;
	}
	public void setPaidBIK(double paidBIK) {
		this.paidBIK = paidBIK;
	}
	
	public PayrollHistory getPayrollHistory() {
		return payrollHistory;
	}
	public void setPayrollHistory(PayrollHistory payrollHistory) {
		this.payrollHistory = payrollHistory;
	}

    public double getPaidOtherDeduction() {
        return paidOtherDeduction;
    }

    public void setPaidOtherDeduction(double paidOtherDeduction) {
        this.paidOtherDeduction = paidOtherDeduction;
    }
    
}
