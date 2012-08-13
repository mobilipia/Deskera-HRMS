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

public class MalaysiaFormEA {
	
	private String id;
	private String serialNumber;
	private String employerERefNumber;
	private String incomeTaxFileNumber;
	private String incomeTaxBranch;
	private String newIdentificationNumber;
	private String oldIdentificationNumber;
	private String accNumberKWSP;
	private Double incomeTaxPaidByEmployer;
	private Double carAndPetrol;
	private String carType;
	private Date carYearMake;
	private String carModel;
	private Double driverWages;
	private Double entertainment;
	private Double handphone;
	private Double maidAndGardener;
	private Double airTicketsForHolidays;
	private Double otherBenefitsForClothingAndFoods;
	private String housingAddress;
	private Double refundsFromKWSPOther;
	private Double compensationLossWork;
	private Double retirementPayment;
	private Double periodicalPayment;
	private Double cp38Deduction;
	private String name;
	private Double portionOfKWSP;
	private String typeOfIncome;
	private Double contributionKWSP;
	private Double amount;
	private Double nonTaxableAmount;
	private Double otherBenefits;
	private Double housingBenefitsWithFurniture;
	private Double housingBenefitsWithKitchen;
	private Double furnitureAndFitting;
	private Double kitchenAndUtensils;
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
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getEmployerERefNumber() {
		return employerERefNumber;
	}
	public void setEmployerERefNumber(String employerERefNumber) {
		this.employerERefNumber = employerERefNumber;
	}
	public String getIncomeTaxFileNumber() {
		return incomeTaxFileNumber;
	}
	public void setIncomeTaxFileNumber(String incomeTaxFileNumber) {
		this.incomeTaxFileNumber = incomeTaxFileNumber;
	}
	public String getIncomeTaxBranch() {
		return incomeTaxBranch;
	}
	public void setIncomeTaxBranch(String incomeTaxBranch) {
		this.incomeTaxBranch = incomeTaxBranch;
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
	public String getAccNumberKWSP() {
		return accNumberKWSP;
	}
	public void setAccNumberKWSP(String accNumberKWSP) {
		this.accNumberKWSP = accNumberKWSP;
	}
	public Double getIncomeTaxPaidByEmployer() {
		return incomeTaxPaidByEmployer;
	}
	public void setIncomeTaxPaidByEmployer(Double incomeTaxPaidByEmployer) {
		this.incomeTaxPaidByEmployer = incomeTaxPaidByEmployer;
	}
	public Double getCarAndPetrol() {
		return carAndPetrol;
	}
	public void setCarAndPetrol(Double carAndPetrol) {
		this.carAndPetrol = carAndPetrol;
	}
	public String getCarType() {
		return carType;
	}
	public void setCarType(String carType) {
		this.carType = carType;
	}
	public Date getCarYearMake() {
		return carYearMake;
	}
	public void setCarYearMake(Date carYearMake) {
		this.carYearMake = carYearMake;
	}
	public String getCarModel() {
		return carModel;
	}
	public void setCarModel(String carModel) {
		this.carModel = carModel;
	}
	public Double getDriverWages() {
		return driverWages;
	}
	public void setDriverWages(Double driverWages) {
		this.driverWages = driverWages;
	}
	public Double getEntertainment() {
		return entertainment;
	}
	public void setEntertainment(Double entertainment) {
		this.entertainment = entertainment;
	}
	public Double getHandphone() {
		return handphone;
	}
	public void setHandphone(Double handphone) {
		this.handphone = handphone;
	}
	public Double getMaidAndGardener() {
		return maidAndGardener;
	}
	public void setMaidAndGardener(Double maidAndGardener) {
		this.maidAndGardener = maidAndGardener;
	}
	public Double getAirTicketsForHolidays() {
		return airTicketsForHolidays;
	}
	public void setAirTicketsForHolidays(Double airTicketsForHolidays) {
		this.airTicketsForHolidays = airTicketsForHolidays;
	}
	public Double getOtherBenefitsForClothingAndFoods() {
		return otherBenefitsForClothingAndFoods;
	}
	public void setOtherBenefitsForClothingAndFoods(
			Double otherBenefitsForClothingAndFoods) {
		this.otherBenefitsForClothingAndFoods = otherBenefitsForClothingAndFoods;
	}
	public String getHousingAddress() {
		return housingAddress;
	}
	public void setHousingAddress(String housingAddress) {
		this.housingAddress = housingAddress;
	}
	public Double getRefundsFromKWSPOther() {
		return refundsFromKWSPOther;
	}
	public void setRefundsFromKWSPOther(Double refundsFromKWSPOther) {
		this.refundsFromKWSPOther = refundsFromKWSPOther;
	}
	public Double getCompensationLossWork() {
		return compensationLossWork;
	}
	public void setCompensationLossWork(Double compensationLossWork) {
		this.compensationLossWork = compensationLossWork;
	}
	public Double getRetirementPayment() {
		return retirementPayment;
	}
	public void setRetirementPayment(Double retirementPayment) {
		this.retirementPayment = retirementPayment;
	}
	public Double getPeriodicalPayment() {
		return periodicalPayment;
	}
	public void setPeriodicalPayment(Double periodicalPayment) {
		this.periodicalPayment = periodicalPayment;
	}
	public Double getCp38Deduction() {
		return cp38Deduction;
	}
	public void setCp38Deduction(Double cp38Deduction) {
		this.cp38Deduction = cp38Deduction;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getPortionOfKWSP() {
		return portionOfKWSP;
	}
	public void setPortionOfKWSP(Double portionOfKWSP) {
		this.portionOfKWSP = portionOfKWSP;
	}
	public String getTypeOfIncome() {
		return typeOfIncome;
	}
	public void setTypeOfIncome(String typeOfIncome) {
		this.typeOfIncome = typeOfIncome;
	}
	public Double getContributionKWSP() {
		return contributionKWSP;
	}
	public void setContributionKWSP(Double contributionKWSP) {
		this.contributionKWSP = contributionKWSP;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Double getNonTaxableAmount() {
		return nonTaxableAmount;
	}
	public void setNonTaxableAmount(Double nonTaxableAmount) {
		this.nonTaxableAmount = nonTaxableAmount;
	}
	public Double getOtherBenefits() {
		return otherBenefits;
	}
	public void setOtherBenefits(Double otherBenefits) {
		this.otherBenefits = otherBenefits;
	}
	public Double getHousingBenefitsWithFurniture() {
		return housingBenefitsWithFurniture;
	}
	public void setHousingBenefitsWithFurniture(Double housingBenefitsWithFurniture) {
		this.housingBenefitsWithFurniture = housingBenefitsWithFurniture;
	}
	public Double getHousingBenefitsWithKitchen() {
		return housingBenefitsWithKitchen;
	}
	public void setHousingBenefitsWithKitchen(Double housingBenefitsWithKitchen) {
		this.housingBenefitsWithKitchen = housingBenefitsWithKitchen;
	}
	public Double getFurnitureAndFitting() {
		return furnitureAndFitting;
	}
	public void setFurnitureAndFitting(Double furnitureAndFitting) {
		this.furnitureAndFitting = furnitureAndFitting;
	}
	public Double getKitchenAndUtensils() {
		return kitchenAndUtensils;
	}
	public void setKitchenAndUtensils(Double kitchenAndUtensils) {
		this.kitchenAndUtensils = kitchenAndUtensils;
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
