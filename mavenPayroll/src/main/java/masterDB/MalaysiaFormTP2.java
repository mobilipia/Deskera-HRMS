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

public class MalaysiaFormTP2 {
	
	private String id;
	private String oldIdentificationNumber;
	private String newIdentificationNumber;
	private String armyOrPoliceNumber;
	private String passportNumber;
	private String incomeTaxLHDNNumber;
	private Double car;
	private Double driver;
	private Double householdItems;
	private Double entertainment;
	private Double gardener;
	private Double maid;
	private Double holidayAllowance;
	private Double membership;
	private Double bik;
	private String signature;
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
	public String getIncomeTaxLHDNNumber() {
		return incomeTaxLHDNNumber;
	}
	public void setIncomeTaxLHDNNumber(String incomeTaxLHDNNumber) {
		this.incomeTaxLHDNNumber = incomeTaxLHDNNumber;
	}
	public Double getCar() {
		return car;
	}
	public void setCar(Double car) {
		this.car = car;
	}
	public Double getDriver() {
		return driver;
	}
	public void setDriver(Double driver) {
		this.driver = driver;
	}
	public Double getHouseholdItems() {
		return householdItems;
	}
	public void setHouseholdItems(Double householdItems) {
		this.householdItems = householdItems;
	}
	public Double getEntertainment() {
		return entertainment;
	}
	public void setEntertainment(Double entertainment) {
		this.entertainment = entertainment;
	}
	public Double getGardener() {
		return gardener;
	}
	public void setGardener(Double gardener) {
		this.gardener = gardener;
	}
	public Double getMaid() {
		return maid;
	}
	public void setMaid(Double maid) {
		this.maid = maid;
	}
	public Double getHolidayAllowance() {
		return holidayAllowance;
	}
	public void setHolidayAllowance(Double holidayAllowance) {
		this.holidayAllowance = holidayAllowance;
	}
	public Double getMembership() {
		return membership;
	}
	public void setMembership(Double membership) {
		this.membership = membership;
	}
	public Double getBik() {
		return bik;
	}
	public void setBik(Double bik) {
		this.bik = bik;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
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
