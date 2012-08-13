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

public class MalaysiaFormTP3 {
	
	private String id;
	private String previousEmployer1;
	private String employerReferenceNo1;
	private String previousEmployer2;
	private String employerReferenceNo2;
	private String oldIdentificationNumber;
	private String newIdentificationNumber;
	private String armyOrPoliceNumber;
	private String passportNumber;
	private String incomeTaxFileNumber;
	private Double freeSampleProductOnDiscount;
	private String employeeLongServiceAward;
	private Double totalContributionToKWSP;
	private Double tuitionfees;
	private Double contributionToPrivatePension;
	private Double totalAllowance;
	private Double otherAllowance;
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
	public String getPreviousEmployer1() {
		return previousEmployer1;
	}
	public void setPreviousEmployer1(String previousEmployer1) {
		this.previousEmployer1 = previousEmployer1;
	}
	public String getEmployerReferenceNo1() {
		return employerReferenceNo1;
	}
	public void setEmployerReferenceNo1(String employerReferenceNo1) {
		this.employerReferenceNo1 = employerReferenceNo1;
	}
	public String getPreviousEmployer2() {
		return previousEmployer2;
	}
	public void setPreviousEmployer2(String previousEmployer2) {
		this.previousEmployer2 = previousEmployer2;
	}
	public String getEmployerReferenceNo2() {
		return employerReferenceNo2;
	}
	public void setEmployerReferenceNo2(String employerReferenceNo2) {
		this.employerReferenceNo2 = employerReferenceNo2;
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
	public String getIncomeTaxFileNumber() {
		return incomeTaxFileNumber;
	}
	public void setIncomeTaxFileNumber(String incomeTaxFileNumber) {
		this.incomeTaxFileNumber = incomeTaxFileNumber;
	}
	public Double getFreeSampleProductOnDiscount() {
		return freeSampleProductOnDiscount;
	}
	public void setFreeSampleProductOnDiscount(Double freeSampleProductOnDiscount) {
		this.freeSampleProductOnDiscount = freeSampleProductOnDiscount;
	}
	public String getEmployeeLongServiceAward() {
		return employeeLongServiceAward;
	}
	public void setEmployeeLongServiceAward(String employeeLongServiceAward) {
		this.employeeLongServiceAward = employeeLongServiceAward;
	}
	public Double getTotalContributionToKWSP() {
		return totalContributionToKWSP;
	}
	public void setTotalContributionToKWSP(Double totalContributionToKWSP) {
		this.totalContributionToKWSP = totalContributionToKWSP;
	}
	public Double getTuitionfees() {
		return tuitionfees;
	}
	public void setTuitionfees(Double tuitionfees) {
		this.tuitionfees = tuitionfees;
	}
	public Double getContributionToPrivatePension() {
		return contributionToPrivatePension;
	}
	public void setContributionToPrivatePension(Double contributionToPrivatePension) {
		this.contributionToPrivatePension = contributionToPrivatePension;
	}
	public Double getTotalAllowance() {
		return totalAllowance;
	}
	public void setTotalAllowance(Double totalAllowance) {
		this.totalAllowance = totalAllowance;
	}
	public Double getOtherAllowance() {
		return otherAllowance;
	}
	public void setOtherAllowance(Double otherAllowance) {
		this.otherAllowance = otherAllowance;
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
