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
package com.krawler.hrms.performance;

import com.krawler.common.admin.User;
import com.krawler.hrms.master.MasterData;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author krawler
 */
public class Appraisalmanagement {

    private String appraisalid;
    private User employee;
    private User manager;
    private String appraisaltype;
    private Date startdate;
    private Date enddate;
    private String appraisalstatus;
    private String employeecomment;
    private double employeegapscore;
    private double employeecompscore;
    private double employeegoalscore;
    private String managercomment;
    private double managergapscore;
    private double managercompscore;
    private double managergoalscore;
    private double totalscore;
    private Date employeesubmitdate;
    private Date managersubmitdate;
    private Integer employeestatus;
    private Integer managerstatus;
    private MasterData performance;
    private Integer reviewstatus;
    private String reviewercomment;
    private Integer salaryrecommend;
    private MasterData newdepartment;
    private MasterData newdesignation;
    private float salaryincrement;
    private MasterData originaldesignation;
    private MasterData originaldepartment;
    private Date reviewersubmitdate;
    private Appraisalcycle appcycle;
    private MasterData reviewdepartment;
    private MasterData reviewdesignation;
    private float reviewsalaryincrement;
    private Integer employeedraft;
    private Integer managerdraft;
    private MasterData empdesid;
    private MasterData managerdesid;
    private Set<CompetencyQuestion> competencyQuestions; 

    public Appraisalmanagement() {
    }

    public String getAppraisalid() {
        return appraisalid;
    }

    public void setAppraisalid(String appraisalid) {
        this.appraisalid = appraisalid;
    }

    public String getAppraisalstatus() {
        return appraisalstatus;
    }

    public void setAppraisalstatus(String appraisalstatus) {
        this.appraisalstatus = appraisalstatus;
    }

    public String getAppraisaltype() {
        return appraisaltype;
    }

    public void setAppraisaltype(String appraisaltype) {
        this.appraisaltype = appraisaltype;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public String getEmployeecomment() {
        return employeecomment;
    }

    public void setEmployeecomment(String employeecomment) {
        this.employeecomment = employeecomment;
    }

    public double getEmployeecompscore() {
        return employeecompscore;
    }

    public void setEmployeecompscore(double employeecompscore) {
        this.employeecompscore = employeecompscore;
    }

    public double getEmployeegapscore() {
        return employeegapscore;
    }

    public void setEmployeegapscore(double employeegapscore) {
        this.employeegapscore = employeegapscore;
    }

    public double getEmployeegoalscore() {
        return employeegoalscore;
    }

    public void setEmployeegoalscore(double employeegoalscore) {
        this.employeegoalscore = employeegoalscore;
    }

    public Integer getEmployeestatus() {
        return employeestatus;
    }

    public void setEmployeestatus(Integer employeestatus) {
        this.employeestatus = employeestatus;
    }

    public Date getEmployeesubmitdate() {
        return employeesubmitdate;
    }

    public void setEmployeesubmitdate(Date employeesubmitdate) {
        this.employeesubmitdate = employeesubmitdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public String getManagercomment() {
        return managercomment;
    }

    public void setManagercomment(String managercomment) {
        this.managercomment = managercomment;
    }

    public double getManagercompscore() {
        return managercompscore;
    }

    public void setManagercompscore(double managercompscore) {
        this.managercompscore = managercompscore;
    }

    public double getManagergapscore() {
        return managergapscore;
    }

    public void setManagergapscore(double managergapscore) {
        this.managergapscore = managergapscore;
    }

    public double getManagergoalscore() {
        return managergoalscore;
    }

    public void setManagergoalscore(double managergoalscore) {
        this.managergoalscore = managergoalscore;
    }

    public Integer getManagerstatus() {
        return managerstatus;
    }

    public void setManagerstatus(Integer managerstatus) {
        this.managerstatus = managerstatus;
    }

    public Date getManagersubmitdate() {
        return managersubmitdate;
    }

    public void setManagersubmitdate(Date managersubmitdate) {
        this.managersubmitdate = managersubmitdate;
    }

    public MasterData getPerformance() {
        return performance;
    }

    public void setPerformance(MasterData performance) {
        this.performance = performance;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public double getTotalscore() {
        return totalscore;
    }

    public void setTotalscore(double totalscore) {
        this.totalscore = totalscore;
    }

    public Integer getReviewstatus() {
        return reviewstatus;
    }

    public void setReviewstatus(Integer reviewstatus) {
        this.reviewstatus = reviewstatus;
    }

    public String getReviewercomment() {
        return reviewercomment;
    }

    public void setReviewercomment(String reviewercomment) {
        this.reviewercomment = reviewercomment;
    }

    public MasterData getNewdepartment() {
        return newdepartment;
    }

    public void setNewdepartment(MasterData newdepartment) {
        this.newdepartment = newdepartment;
    }

    public MasterData getNewdesignation() {
        return newdesignation;
    }

    public void setNewdesignation(MasterData newdesignation) {
        this.newdesignation = newdesignation;
    }

    public Integer getSalaryrecommend() {
        return salaryrecommend;
    }

    public void setSalaryrecommend(Integer salaryrecommend) {
        this.salaryrecommend = salaryrecommend;
    }

    public float getSalaryincrement() {
        return salaryincrement;
    }

    public void setSalaryincrement(float salaryincrement) {
        this.salaryincrement = salaryincrement;
    }

    public Appraisalcycle getAppcycle() {
        return appcycle;
    }

    public void setAppcycle(Appraisalcycle appcycle) {
        this.appcycle = appcycle;
    }

    public MasterData getReviewdepartment() {
        return reviewdepartment;
    }

    public void setReviewdepartment(MasterData reviewdepartment) {
        this.reviewdepartment = reviewdepartment;
    }

    public MasterData getReviewdesignation() {
        return reviewdesignation;
    }

    public void setReviewdesignation(MasterData reviewdesignation) {
        this.reviewdesignation = reviewdesignation;
    }

    public float getReviewsalaryincrement() {
        return reviewsalaryincrement;
    }

    public void setReviewsalaryincrement(float reviewsalaryincrement) {
        this.reviewsalaryincrement = reviewsalaryincrement;
    }

    public MasterData getOriginaldepartment() {
        return originaldepartment;
    }

    public void setOriginaldepartment(MasterData originaldepartment) {
        this.originaldepartment = originaldepartment;
    }

    public MasterData getOriginaldesignation() {
        return originaldesignation;
    }

    public void setOriginaldesignation(MasterData originaldesignation) {
        this.originaldesignation = originaldesignation;
    }

    public Date getReviewersubmitdate() {
        return reviewersubmitdate;
    }

    public void setReviewersubmitdate(Date reviewersubmitdate) {
        this.reviewersubmitdate = reviewersubmitdate;
    }

    public Integer getEmployeedraft() {
        return employeedraft;
    }

    public void setEmployeedraft(Integer employeedraft) {
        this.employeedraft = employeedraft;
    }

    public Integer getManagerdraft() {
        return managerdraft;
    }

    public void setManagerdraft(Integer managerdraft) {
        this.managerdraft = managerdraft;
    }
    
    public MasterData getEmpdesid() {
		return empdesid;
	}

	public void setEmpdesid(MasterData empdesid) {
		this.empdesid = empdesid;
	}

	public MasterData getManagerdesid() {
		return managerdesid;
	}

	public void setManagerdesid(MasterData managerdesid) {
		this.managerdesid = managerdesid;
	}

	public Set<CompetencyQuestion> getCompetencyQuestions() {
		return competencyQuestions;
	}

	public void setCompetencyQuestions(Set<CompetencyQuestion> competencyQuestions) {
		this.competencyQuestions = competencyQuestions;
	}
}
