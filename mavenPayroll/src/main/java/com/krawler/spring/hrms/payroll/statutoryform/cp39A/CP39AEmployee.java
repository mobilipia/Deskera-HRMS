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
package com.krawler.spring.hrms.payroll.statutoryform.cp39A;

public class CP39AEmployee {
	private String employerReferenceNumber;
	private String number;
	private String incomeTaxFileNumber;
	private String fullName;
	private String oldIdentificationNumber;
	private String newIdentificationNumber;
	private String employeeNumber;
	private String passportNumber;
	private String countryCode;
	private String deductionAmountForPCB;
	private String deductionAmountForCP38;
	private String totalForPCB;
	private String totalForCP38;
	private String grandTotal;
	
	public String getEmployerReferenceNumber() {
		return employerReferenceNumber;
	}
	public void setEmployerReferenceNumber(String employerReferenceNumber) {
		this.employerReferenceNumber = employerReferenceNumber;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getIncomeTaxFileNumber() {
		return incomeTaxFileNumber;
	}
	public void setIncomeTaxFileNumber(String incomeTaxFileNumber) {
		this.incomeTaxFileNumber = incomeTaxFileNumber;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getOldIdentificationNumber() {
		return oldIdentificationNumber;
	}
	public void setOldIdentificationNumber(String oldIdentificationNumber) {
		this.oldIdentificationNumber = oldIdentificationNumber;
	}
	public String getNewIdentificationNumber() {
		return newIdentificationNumber;
	}
	public void setNewIdentificationNumber(String newIdentificationNumber) {
		this.newIdentificationNumber = newIdentificationNumber;
	}
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public String getPassportNumber() {
		return passportNumber;
	}
	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getDeductionAmountForPCB() {
		return deductionAmountForPCB;
	}
	public void setDeductionAmountForPCB(String deductionAmountForPCB) {
		this.deductionAmountForPCB = deductionAmountForPCB;
	}
	public String getDeductionAmountForCP38() {
		return deductionAmountForCP38;
	}
	public void setDeductionAmountForCP38(String deductionAmountForCP38) {
		this.deductionAmountForCP38 = deductionAmountForCP38;
	}
	public String getTotalForPCB() {
		return totalForPCB;
	}
	public void setTotalForPCB(String totalForPCB) {
		this.totalForPCB = totalForPCB;
	}
	public String getTotalForCP38() {
		return totalForCP38;
	}
	public void setTotalForCP38(String totalForCP38) {
		this.totalForCP38 = totalForCP38;
	}
	public String getGrandTotal() {
		return grandTotal;
	}
	public void setGrandTotal(String grandTotal) {
		this.grandTotal = grandTotal;
	}
}
