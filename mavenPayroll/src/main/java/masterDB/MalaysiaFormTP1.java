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

public class MalaysiaFormTP1 {
	
	private String id;
	private String newIdentificationNumber;
	private String oldIdentificationNumber;
	private String armyOrPoliceNumber;
	private String passportNumber;
	private String incomeTaxNumber;
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
	public String getNewIdentificationNumber() {
		return newIdentificationNumber;
	}
	public void setNewIdentificationNumber(String newIdentificationNumber) {
		this.newIdentificationNumber = newIdentificationNumber;
	}
	public String getOldIdentificationNumber() {
		return oldIdentificationNumber;
	}
	public void setOldIdentificationNumber(String oldIdentificationNumber) {
		this.oldIdentificationNumber = oldIdentificationNumber;
	}
	public String getArmyOrPoliceNumber() {
		return armyOrPoliceNumber;
	}
	public void setArmyOrPoliceNumber(String armyOrPoliceNumber) {
		this.armyOrPoliceNumber = armyOrPoliceNumber;
	}
	public String getPassportNumber() {
		return passportNumber;
	}
	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}
	public String getIncomeTaxNumber() {
		return incomeTaxNumber;
	}
	public void setIncomeTaxNumber(String incomeTaxNumber) {
		this.incomeTaxNumber = incomeTaxNumber;
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
