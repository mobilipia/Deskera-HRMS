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

import java.util.Date;

import com.krawler.common.admin.Useraccount;

public class MalaysiaFormPCB2 {
	
	private String id;
	private Double deductionAmountForCP38;
	private String taxResitForPCB;
	private Date taxResitForPCBDate;
	private String taxResitForCP38;
	private Date taxResitForCP38Date;
	private String newIdentificationNumber;
	private String incomeTaxFileNumber;
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
	public Double getDeductionAmountForCP38() {
		return deductionAmountForCP38;
	}
	public void setDeductionAmountForCP38(Double deductionAmountForCP38) {
		this.deductionAmountForCP38 = deductionAmountForCP38;
	}
	public String getTaxResitForPCB() {
		return taxResitForPCB;
	}
	public void setTaxResitForPCB(String taxResitForPCB) {
		this.taxResitForPCB = taxResitForPCB;
	}
	public Date getTaxResitForPCBDate() {
		return taxResitForPCBDate;
	}
	public void setTaxResitForPCBDate(Date taxResitForPCBDate) {
		this.taxResitForPCBDate = taxResitForPCBDate;
	}
	public String getTaxResitForCP38() {
		return taxResitForCP38;
	}
	public void setTaxResitForCP38(String taxResitForCP38) {
		this.taxResitForCP38 = taxResitForCP38;
	}
	public Date getTaxResitForCP38Date() {
		return taxResitForCP38Date;
	}
	public void setTaxResitForCP38Date(Date taxResitForCP38Date) {
		this.taxResitForCP38Date = taxResitForCP38Date;
	}
	public String getNewIdentificationNumber() {
		return newIdentificationNumber;
	}
	public void setNewIdentificationNumber(String newIdentificationNumber) {
		this.newIdentificationNumber = newIdentificationNumber;
	}
	public String getIncomeTaxFileNumber() {
		return incomeTaxFileNumber;
	}
	public void setIncomeTaxFileNumber(String incomeTaxFileNumber) {
		this.incomeTaxFileNumber = incomeTaxFileNumber;
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
