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
package com.krawler.hrms.recruitment;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.hrms.master.MasterData;
import java.util.Date;

/**
 *
 * @author krawler
 */
public class Allapplications {

    private String id;
    private Positionmain position;
    private Jobapplicant jobapplicant;
    private User employee;
    private Date applydate;
    private Date joiningdate;
    private String interviewtime;
    private String status;
    public static String Rejected = "Rejected";
    private Date interviewdate;
    private MasterData rank;
    private MasterData callback;
    private String recruiter;
    private String interviewplace;
    private String interviewcomment;
    private Company company;
    private Integer delflag;
    private Integer applicationflag;
    private Integer rejectedbefore;
    private String statuscomment;
    private Integer employeetype;
    private User contactperson;
    private ConfigRecruitmentData configjobapplicant;
    public Allapplications() {
    }

        public Allapplications(String id, Positionmain position, Jobapplicant jobapplicant, User employee, Date applydate, Date joiningdate, String interviewtime, String status, Date interviewdate, MasterData rank, MasterData callback, String recruiter, String interviewplace, String interviewcomment, Company company, Integer delflag, Integer applicationflag, Integer rejectedbefore, String statuscomment, Integer employeetype, User contactperson) {
        this.id = id;
        this.position = position;
        this.jobapplicant = jobapplicant;
        this.employee = employee;
        this.applydate = applydate;
        this.joiningdate = joiningdate;
        this.interviewtime = interviewtime;
        this.status = status;
        this.interviewdate = interviewdate;
        this.rank = rank;
        this.callback = callback;
        this.recruiter = recruiter;
        this.interviewplace = interviewplace;
        this.interviewcomment = interviewcomment;
        this.company = company;
        this.delflag = delflag;
        this.applicationflag = applicationflag;
        this.rejectedbefore = rejectedbefore;
        this.statuscomment = statuscomment;
        this.employeetype = employeetype;
        this.contactperson = contactperson;
    }
      
    public String getInterviewtime() {
        return interviewtime;
    }

    public void setInterviewtime(String interviewtime) {
        this.interviewtime = interviewtime;
    }

    public String getInterviewcomment() {
        return interviewcomment;
    }

    public void setInterviewcomment(String interviewcomment) {
        this.interviewcomment = interviewcomment;
    }

    public String getInterviewplace() {
        return interviewplace;
    }

    public void setInterviewplace(String interviewplace) {
        this.interviewplace = interviewplace;
    }

    public Date getApplydate() {
        return applydate;
    }

    public void setApplydate(Date applydate) {
        this.applydate = applydate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getInterviewdate() {
        return interviewdate;
    }

    public void setInterviewdate(Date interviewdate) {
        this.interviewdate = interviewdate;
    }

    public Date getJoiningdate() {
        return joiningdate;
    }

    public void setJoiningdate(Date joiningdate) {
        this.joiningdate = joiningdate;
    }

    public Positionmain getPosition() {
        return position;
    }

    public void setPosition(Positionmain position) {
        this.position = position;
    }
 
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Jobapplicant getJobapplicant() {
        return jobapplicant;
    }

    public void setJobapplicant(Jobapplicant jobapplicant) {
        this.jobapplicant = jobapplicant;
    }

    public Integer getDelflag() {
        return delflag;
    }

    public Integer getApplicationflag() {
        return applicationflag;
    }

    public void setApplicationflag(Integer applicationflag) {
        this.applicationflag = applicationflag;
    }

    public void setDelflag(Integer delflag) {
        this.delflag = delflag;
    }

    public String getRecruiter() {
        return recruiter;
    }

    public void setRecruiter(String recruiter) {
        this.recruiter = recruiter;
    }
   
    public Integer getRejectedbefore() {
        return rejectedbefore;
    }

    public void setRejectedbefore(Integer rejectedbefore) {
        this.rejectedbefore = rejectedbefore;
    }

    public String getStatuscomment() {
        return statuscomment;
    }

    public void setStatuscomment(String statuscomment) {
        this.statuscomment = statuscomment;
    }

    public MasterData getCallback() {
        return callback;
    }

    public void setCallback(MasterData callback) {
        this.callback = callback;
    }

    public MasterData getRank() {
        return rank;
    }

    public void setRank(MasterData rank) {
        this.rank = rank;
    }

    public User getEmployee() {
        return employee;
    }
    public User getContactperson() {
        return contactperson;
    }
    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public Integer getEmployeetype() {
        return employeetype;
    }

    public void setEmployeetype(Integer employeetype) {
        this.employeetype = employeetype;
    }
    public void setContactperson(User contactperson) {
        this.contactperson = contactperson;
    }
    public void setConfigjobapplicant(ConfigRecruitmentData configjobapplicant) {
        this.configjobapplicant = configjobapplicant;
    }

    public ConfigRecruitmentData getConfigjobapplicant() {
        return configjobapplicant;
    }
}
