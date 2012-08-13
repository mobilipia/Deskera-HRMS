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

package com.krawler.common.admin;

/**
 *
 * @author krawler
 */
public class CompanyPreferences {
      private  String companyid;
      private Company company;
      private boolean  selfappraisal;
      private boolean  competency;
      private boolean  timesheetjob;
      private boolean  goal;
      private boolean  annmanager;
      private boolean  approveappraisal;
      private boolean  promotion;
      private boolean  weightage;
      private boolean  reviewappraisal;
      private boolean  partial;
      private boolean  fullupdates;
      private boolean  modaverage;
      private boolean  overallcomments;
      private  String defaultapps;
      private  String payrollbase;
      private String empidformat;   // @@HRMS field
      private String jobidformat;   // @@HRMS field
      private int financialmonth;   // @@HRMS field
      private int weeklyholidays;   // @@HRMS field
      private long subscriptionCode;//@@HRMS not used
      private  String emailNotification;
      private boolean  approvesalary;
      private int holidaycode;
      private boolean blockemployees;

    public CompanyPreferences() {
    }

    public boolean isTimesheetjob() {
        return timesheetjob;
    }

    public void setTimesheetjob(boolean timesheetjob) {
        this.timesheetjob = timesheetjob;
    }

    public String getPayrollbase() {
        return payrollbase;
    }

    public void setPayrollbase(String payrollbase) {
        this.payrollbase = payrollbase;
    }

    public boolean isOverallcomments() {
        return overallcomments;
    }

    public void setOverallcomments(boolean overallcomments) {
        this.overallcomments = overallcomments;
    }

    public String getEmpidformat() {
        return empidformat;
    }

    public void setEmpidformat(String empidformat) {
        this.empidformat = empidformat;
    }

    public String getJobidformat() {
        return jobidformat;
    }

    public void setJobidformat(String jobidformat) {
        this.jobidformat = jobidformat;
    }

    public long getSubscriptionCode() {
        return subscriptionCode;
    }

    public void setSubscriptionCode(long subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getCompanyid() {
        return companyid;
    }

    public int getWeeklyholidays() {
        return weeklyholidays;
    }

    public void setWeeklyholidays(int weeklyholidays) {
        this.weeklyholidays = weeklyholidays;
    }

    public void setCompanyid(String companyid) {
        this.companyid = companyid;
    }

    public boolean isCompetency() {
        return competency;
    }

    public void setCompetency(boolean competency) {
        this.competency = competency;
    }

    public boolean isGoal() {
        return goal;
    }

    public void setGoal(boolean goal) {
        this.goal = goal;
    }

    public boolean isSelfappraisal() {
        return selfappraisal;
    }

    public void setSelfappraisal(boolean selfappraisal) {
        this.selfappraisal = selfappraisal;
    }

    public boolean isAnnmanager() {
        return annmanager;
    }

    public void setAnnmanager(boolean annmanager) {
        this.annmanager = annmanager;
    }

    public boolean isApproveappraisal() {
        return approveappraisal;
    }

    public void setApproveappraisal(boolean approveappraisal) {
        this.approveappraisal = approveappraisal;
    }

    public boolean isPromotion() {
        return promotion;
    }

    public void setPromotion(boolean promotion) {
        this.promotion = promotion;
    }

    public boolean isWeightage() {
        return weightage;
    }

    public void setWeightage(boolean weightage) {
        this.weightage = weightage;
    }

    public boolean isReviewappraisal() {
        return reviewappraisal;
    }

    public void setReviewappraisal(boolean reviewappraisal) {
        this.reviewappraisal = reviewappraisal;
    }

    public boolean isPartial() {
        return partial;
    }

    public void setPartial(boolean partial) {
        this.partial = partial;
    }

    public boolean isFullupdates() {
        return fullupdates;
    }

    public void setFullupdates(boolean fullupdates) {
        this.fullupdates = fullupdates;
    }

    public boolean isModaverage() {
        return modaverage;
    }

    public void setModaverage(boolean modaverage) {
        this.modaverage = modaverage;
    }

    public String getDefaultapps() {
        return defaultapps;
    }

    public void setDefaultapps(String defaultapps) {
        this.defaultapps = defaultapps;
    }

    public int getFinancialmonth() {
        return financialmonth;
    }

    public void setFinancialmonth(int financialmonth) {
        this.financialmonth = financialmonth;
    }

	public String getEmailNotification() {
		return emailNotification;
	}

	public void setEmailNotification(String emailNotification) {
		this.emailNotification = emailNotification;
	}

    public boolean isApprovesalary() {
        return approvesalary;
    }

    public void setApprovesalary(boolean approvesalary) {
        this.approvesalary = approvesalary;
    }

    public int getHolidaycode() {
        return holidaycode;
    }

    public void setHolidaycode(int holidaycode) {
        this.holidaycode = holidaycode;
    }

    public boolean isBlockemployees() {
		return blockemployees;
	}

	public void setBlockemployees(boolean blockemployees) {
		this.blockemployees = blockemployees;
	}
}
