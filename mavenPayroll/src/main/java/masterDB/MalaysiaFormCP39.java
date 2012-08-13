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

import com.krawler.common.admin.Useraccount;

public class MalaysiaFormCP39 {
	
	private String id;
	private String incomeTaxFileNumber;
	private String oldIdentificationNumber;
	private String newIdentificationNumber;
	private String passportNumber;
	private String countryCode;
	private Double deductionAmountForCP38;
	private Integer month;
	private Integer year;
	private Useraccount useraccount;
	private int authorizeStatus;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIncomeTaxFileNumber() {
		return incomeTaxFileNumber;
	}
	public void setIncomeTaxFileNumber(String incomeTaxFileNumber) {
		this.incomeTaxFileNumber = incomeTaxFileNumber;
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
	public Double getDeductionAmountForCP38() {
		return deductionAmountForCP38;
	}
	public void setDeductionAmountForCP38(Double deductionAmountForCP38) {
		this.deductionAmountForCP38 = deductionAmountForCP38;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public Useraccount getUseraccount() {
		return useraccount;
	}
	public void setUseraccount(Useraccount useraccount) {
		this.useraccount = useraccount;
	}

    public int getAuthorizeStatus() {
        return authorizeStatus;
    }

    public void setAuthorizeStatus(int authorizeStatus) {
        this.authorizeStatus = authorizeStatus;
    }
    
}
